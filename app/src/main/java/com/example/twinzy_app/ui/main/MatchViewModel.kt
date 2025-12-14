package com.example.twinzy_app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twinzy_app.data.model.Match
import com.example.twinzy_app.data.model.User
import com.example.twinzy_app.data.repository.MatchRepository
import com.example.twinzy_app.data.repository.UserRepository
import com.example.twinzy_app.data.repository.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll // Ajouté pour awaitAll()
import kotlinx.coroutines.coroutineScope // Ajouté pour le scope concurrent
import javax.inject.Inject

data class MatchWithUser(
    val match: com.example.twinzy_app.data.model.Match,
    val otherUser: com.example.twinzy_app.data.model.User,
    val lastMessage: com.example.twinzy_app.data.model.Message? = null
)

sealed class MatchesUiState {
    object Loading : MatchesUiState()
    data class Success(val matches: List<MatchWithUser>) : MatchesUiState()
    data class Error(val message: String) : MatchesUiState()
    object Empty : MatchesUiState()
}

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private val matchesWithMessages = MutableStateFlow<Map<String, MatchWithUser>>(emptyMap())

    init {
        loadMatches()
    }

    fun loadMatches() {
        val currentUserId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = MatchesUiState.Loading

            matchRepository.observeMatches(currentUserId)
                .catch { e ->
                    _uiState.value = MatchesUiState.Error(e.message ?: "Failed to load matches")
                }
                .collect { matches ->
                    if (matches.isEmpty()) {
                        _uiState.value = MatchesUiState.Empty
                    } else {
                        // Load users and start observing messages for each match
                        val matchesWithUsers = coroutineScope {
                            matches.mapNotNull { match ->
                                val otherUserId = match.users.firstOrNull { it != currentUserId }
                                otherUserId?.let {
                                    async {
                                        userRepository.getUserById(it).getOrNull()?.let { user ->
                                            val matchWithUser = MatchWithUser(match, user, null)
                                            // Start observing messages for this match
                                            observeMatchMessages(match.matchId, matchWithUser)
                                            matchWithUser
                                        }
                                    }
                                }
                            }.awaitAll().filterNotNull()
                        }

                        // Update initial state
                        val initialMap = matchesWithUsers.associateBy { it.match.matchId }
                        matchesWithMessages.value = initialMap
                        
                        if (matchesWithUsers.isEmpty()) {
                            _uiState.value = MatchesUiState.Empty
                        } else {
                            _uiState.value = MatchesUiState.Success(matchesWithUsers)
                        }
                    }
                    _isRefreshing.value = false
                }
        }
    }
    
    private fun observeMatchMessages(matchId: String, initialMatch: MatchWithUser) {
        viewModelScope.launch {
            chatRepository.observeLastMessage(matchId)
                .collect { lastMessage ->
                    val updatedMatch = initialMatch.copy(lastMessage = lastMessage)
                    val currentMatches = matchesWithMessages.value.toMutableMap()
                    currentMatches[matchId] = updatedMatch
                    matchesWithMessages.value = currentMatches
                    
                    // Update UI state with real-time data
                    val matchesList = currentMatches.values.toList()
                    _uiState.value = MatchesUiState.Success(matchesList)
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadMatches()
        }
    }

    fun unmatch(matchId: String) {
        viewModelScope.launch {
            matchRepository.unmatch(matchId)
                .onSuccess {
                    // Le Match sera retiré automatiquement par l'observateur Firestore
                }
                .onFailure { e ->
                    // Gérer l'erreur si l'unmatch échoue
                    // Vous pouvez ajouter ici un flow d'erreur pour les messages Toast ou Dialogs
                }
        }
    }


}