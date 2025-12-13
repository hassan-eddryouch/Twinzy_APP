package com.example.twinzy_app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twinzy_app.data.model.*
import com.example.twinzy_app.data.repository.AuthRepository
import com.example.twinzy_app.data.repository.DiscoverRepository
import com.example.twinzy_app.data.repository.SwipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiscoverState(
    val isLoading: Boolean = false,
    val currentUserProfile: UserProfile? = null,
    val profiles: List<UserProfile> = emptyList(),
    val swipeResult: SwipeResult? = null,
    val error: String? = null,
    val isMatch: Boolean = false
)

data class SwipeResult(
    val action: SwipeAction,
    val targetUserId: String,
    val isMatch: Boolean = false,
    val matchId: String? = null,
    val userProfile: UserProfile? = null
)

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    private val swipeRepository: SwipeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DiscoverState())
    val uiState: StateFlow<DiscoverState> = _uiState.asStateFlow()
    
    private var currentUser: User? = null
    
    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                currentUser = user
                if (user != null) {
                    loadProfiles()
                }
            }
        }
    }
    
    private fun loadProfiles() {
        viewModelScope.launch {
            currentUser?.let { user ->
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                discoverRepository.getDiscoverProfiles(user.uid)
                    .onSuccess { profiles ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            profiles = profiles,
                            currentUserProfile = profiles.firstOrNull()
                        )
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
            }
        }
    }
    
    fun swipeLeft(targetUserId: String) {
        performSwipe(SwipeAction.DISLIKE, targetUserId)
    }
    
    fun swipeRight(targetUserId: String) {
        performSwipe(SwipeAction.LIKE, targetUserId)
    }
    
    fun superLike(targetUserId: String) {
        performSwipe(SwipeAction.SUPER_LIKE, targetUserId)
    }
    
    private fun performSwipe(action: SwipeAction, targetUserId: String) {
        viewModelScope.launch {
            currentUser?.let { user ->
                val swipe = Swipe(
                    userId = user.uid,
                    targetUserId = targetUserId,
                    isLike = action == SwipeAction.LIKE || action == SwipeAction.SUPER_LIKE,
                    isSuperLike = action == SwipeAction.SUPER_LIKE,
                    timestamp = System.currentTimeMillis()
                )
                
                swipeRepository.swipeUser(swipe)
                    .onSuccess { match ->
                        val remainingProfiles = _uiState.value.profiles.drop(1)
                        val currentProfile = _uiState.value.currentUserProfile
                        
                        val swipeResult = SwipeResult(
                            action = action,
                            targetUserId = targetUserId,
                            isMatch = match != null,
                            matchId = match?.matchId,
                            userProfile = currentProfile
                        )
                        
                        _uiState.value = _uiState.value.copy(
                            profiles = remainingProfiles,
                            currentUserProfile = remainingProfiles.firstOrNull(),
                            swipeResult = swipeResult,
                            isMatch = match != null
                        )
                        
                        if (remainingProfiles.isEmpty()) {
                            loadProfiles()
                        }
                    }
                    .onFailure { exception ->
                        // Still move to next profile even on error
                        val remainingProfiles = _uiState.value.profiles.drop(1)
                        _uiState.value = _uiState.value.copy(
                            profiles = remainingProfiles,
                            currentUserProfile = remainingProfiles.firstOrNull(),
                            error = exception.message
                        )
                        
                        if (remainingProfiles.isEmpty()) {
                            loadProfiles()
                        }
                    }
            }
        }
    }
    
    fun clearSwipeResult() {
        _uiState.value = _uiState.value.copy(swipeResult = null, isMatch = false)
    }
    
    fun clearMatchResult() {
        _uiState.value = _uiState.value.copy(isMatch = false, swipeResult = null)
    }
    
    fun refresh() {
        loadProfiles()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}