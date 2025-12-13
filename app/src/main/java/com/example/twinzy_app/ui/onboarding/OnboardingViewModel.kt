package com.example.twinzy_app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twinzy_app.data.manager.PreferencesManager
import com.example.twinzy_app.utils.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val errorHandler: ErrorHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState.asStateFlow()
    
    fun nextPage() {
        val currentState = _uiState.value
        if (currentState.currentPage < 2) {
            _uiState.value = currentState.copy(
                currentPage = currentState.currentPage + 1,
                error = null
            )
        }
    }
    
    fun previousPage() {
        val currentState = _uiState.value
        if (currentState.currentPage > 0) {
            _uiState.value = currentState.copy(
                currentPage = currentState.currentPage - 1,
                error = null
            )
        }
    }
    
    fun completeOnboarding() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                preferencesManager.setOnboardingCompleted()
                
                _uiState.value = _uiState.value.copy(isLoading = false)
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
}