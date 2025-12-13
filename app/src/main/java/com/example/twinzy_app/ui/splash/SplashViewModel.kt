package com.example.twinzy_app.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twinzy_app.data.manager.PreferencesManager
import com.example.twinzy_app.utils.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SplashState(
    val isLoading: Boolean = true,
    val isOnboardingCompleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val errorHandler: ErrorHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SplashState())
    val uiState: StateFlow<SplashState> = _uiState.asStateFlow()
    
    init {
        loadInitialState()
    }
    
    private fun loadInitialState() {
        viewModelScope.launch {
            try {
                combine(
                    preferencesManager.isOnboardingCompleted,
                    preferencesManager.isProfileCreated
                ) { onboardingCompleted, profileCreated ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOnboardingCompleted = onboardingCompleted
                    )
                }.collect { }
            } catch (e: Exception) {
                val error = errorHandler.handleException(e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorHandler.createSafeErrorMessage(error)
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun enableDevMode() {
        viewModelScope.launch {
            preferencesManager.setDevMode(true)
        }
    }
}