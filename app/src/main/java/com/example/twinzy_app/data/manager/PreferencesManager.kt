package com.example.twinzy_app.data.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "twinzy_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val PROFILE_CREATED = booleanPreferencesKey("profile_created")
        private val USER_ID = stringPreferencesKey("user_id")
        private val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        private val DEV_MODE_ENABLED = booleanPreferencesKey("dev_mode_enabled")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }

    val isProfileCreated: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PROFILE_CREATED] ?: false
        }

    val userId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID]
        }

    val selectedLanguage: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_LANGUAGE] ?: "en"
        }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = true
        }
    }

    suspend fun markOnboardingComplete() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = true
        }
    }

    suspend fun setProfileCreated(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_CREATED] = true
            preferences[USER_ID] = userId
        }
    }

    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_CREATED] = false
            preferences[USER_ID] = ""
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_LANGUAGE] = language
        }
    }

    val isDevModeEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DEV_MODE_ENABLED] ?: false
        }

    suspend fun setDevMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DEV_MODE_ENABLED] = enabled
        }
    }
}