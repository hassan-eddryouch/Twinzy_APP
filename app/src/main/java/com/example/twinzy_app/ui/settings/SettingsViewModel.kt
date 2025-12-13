package com.example.twinzy_app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twinzy_app.data.manager.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

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
}

data class SettingsUiState(
    val selectedLanguage: String = "en"
)