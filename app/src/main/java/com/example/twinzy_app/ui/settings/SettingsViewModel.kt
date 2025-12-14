package com.example.twinzy_app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twinzy_app.data.manager.PreferencesManager
import com.example.twinzy_app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _signOutEvent = Channel<Unit>()
    val signOutEvent = _signOutEvent.receiveAsFlow()
    
    private val _navigateToAuthEvent = Channel<Unit>()
    val navigateToAuthEvent = _navigateToAuthEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            preferencesManager.selectedLanguage.collect { language ->
                _uiState.value = _uiState.value.copy(selectedLanguage = language)
            }
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
        }
    }

    fun onSignOutClicked() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                _signOutEvent.send(Unit)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
    
    fun onDeleteAccountConfirmed() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                authRepository.deleteAccount().getOrThrow()
                _navigateToAuthEvent.send(Unit)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to delete account"
                )
            }
        }
    }
}

data class SettingsUiState(
    val selectedLanguage: String = "en",
    val isLoading: Boolean = false,
    val error: String? = null
)