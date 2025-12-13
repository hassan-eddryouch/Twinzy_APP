package com.example.twinzy_app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twinzy_app.data.model.Message
import com.example.twinzy_app.data.model.MessageType
import com.example.twinzy_app.data.model.User
import com.example.twinzy_app.data.repository.ChatRepository
import com.example.twinzy_app.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatUiState {
    object Loading : ChatUiState()
    data class Success(
        val messages: List<Message>,
        val otherUser: User
    ) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()
    
    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()
    
    private var currentMatchId: String = ""
    private var otherUserId: String = ""
    private var currentUserId: String = auth.currentUser?.uid ?: ""
    private var otherUser: User? = null
    
    fun initialize(matchId: String, otherUserId: String) {
        this.currentMatchId = matchId
        this.otherUserId = otherUserId
        this.currentUserId = auth.currentUser?.uid ?: ""
        
        loadChatData()
    }
    
    private fun loadChatData() {
        if (currentMatchId.isEmpty() || otherUserId.isEmpty()) {
            _uiState.value = ChatUiState.Error("Invalid chat parameters")
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = ChatUiState.Loading
                
                // Load other user details first
                val userResult = userRepository.getUserById(otherUserId)
                if (userResult.isFailure) {
                    _uiState.value = ChatUiState.Error("Failed to load user details")
                    return@launch
                }
                
                otherUser = userResult.getOrNull()
                if (otherUser == null) {
                    _uiState.value = ChatUiState.Error("User not found")
                    return@launch
                }
                
                // Start observing messages
                chatRepository.observeMessages(currentMatchId)
                    .catch { e ->
                        _uiState.value = ChatUiState.Error(e.message ?: "Failed to load messages")
                    }
                    .collect { messages ->
                        _uiState.value = ChatUiState.Success(
                            messages = messages,
                            otherUser = otherUser!!
                        )
                    }
                    
            } catch (e: Exception) {
                _uiState.value = ChatUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun updateMessageText(text: String) {
        _messageText.value = text
    }
    
    fun sendMessage() {
        val text = _messageText.value.trim()
        if (text.isBlank() || _isSending.value || currentMatchId.isBlank() || currentUserId.isBlank()) {
            return
        }
        
        viewModelScope.launch {
            try {
                _isSending.value = true
                
                val message = Message(
                    matchId = currentMatchId,
                    senderId = currentUserId,
                    receiverId = otherUserId,
                    text = text,
                    timestamp = System.currentTimeMillis(),
                    messageType = MessageType.TEXT
                )
                
                chatRepository.sendMessage(message)
                _messageText.value = ""
                
            } catch (e: Exception) {
                // Silently handle error
            } finally {
                _isSending.value = false
            }
        }
    }
    
    fun onNavigateToProfile(): String? {
        return otherUser?.uid
    }
    
    fun refresh() {
        if (currentMatchId.isNotEmpty() && otherUserId.isNotEmpty()) {
            loadChatData()
        }
    }
}