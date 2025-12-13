package com.example.twinzy_app.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twinzy_app.data.manager.PreferencesManager
import com.example.twinzy_app.data.model.Gender
import com.example.twinzy_app.data.model.User
import com.example.twinzy_app.data.repository.AuthRepository
import com.example.twinzy_app.data.repository.UserRepository
import com.example.twinzy_app.utils.ErrorHandler
import com.example.twinzy_app.utils.InputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileCreationState(
    val isLoading: Boolean = false,
    val currentStep: Int = 0,
    val name: String = "",
    val age: String = "",
    val gender: Gender = Gender.MALE,
    val bio: String = "",
    val interests: List<String> = emptyList(),
    val photos: List<String> = emptyList(),
    val isUploadingPhoto: Boolean = false,
    val error: String? = null,
    val isCompleted: Boolean = false
)

@HiltViewModel
class ProfileCreationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val preferencesManager: PreferencesManager,
    private val inputValidator: InputValidator,
    private val errorHandler: ErrorHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileCreationState())
    val uiState: StateFlow<ProfileCreationState> = _uiState.asStateFlow()
    
    fun updateName(name: String) {
        val sanitized = inputValidator.sanitizeInput(name)
        _uiState.value = _uiState.value.copy(name = sanitized, error = null)
    }
    
    fun updateAge(age: String) {
        val sanitized = age.filter { it.isDigit() }.take(2)
        _uiState.value = _uiState.value.copy(age = sanitized, error = null)
    }
    
    fun updateGender(gender: Gender) {
        _uiState.value = _uiState.value.copy(gender = gender, error = null)
    }
    
    fun updateBio(bio: String) {
        val sanitized = inputValidator.sanitizeInput(bio)
        _uiState.value = _uiState.value.copy(bio = sanitized, error = null)
    }
    
    fun addInterest(interest: String) {
        val sanitized = inputValidator.sanitizeInput(interest)
        val currentInterests = _uiState.value.interests
        
        if (currentInterests.size < 10 && sanitized.isNotBlank() && !currentInterests.contains(sanitized)) {
            _uiState.value = _uiState.value.copy(
                interests = currentInterests + sanitized,
                error = null
            )
        }
    }
    
    fun removeInterest(interest: String) {
        val currentInterests = _uiState.value.interests
        _uiState.value = _uiState.value.copy(
            interests = currentInterests - interest,
            error = null
        )
    }
    
    fun uploadPhoto(uri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isUploadingPhoto = true, error = null)
                
                val userId = authRepository.getCurrentUserId()
                    ?: throw SecurityException("User not authenticated")
                
                userRepository.uploadProfileImage(uri, userId)
                    .onSuccess { imageUrl ->
                        val currentPhotos = _uiState.value.photos
                        if (currentPhotos.size < 6) {
                            _uiState.value = _uiState.value.copy(
                                photos = currentPhotos + imageUrl,
                                isUploadingPhoto = false
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isUploadingPhoto = false,
                                error = "Maximum 6 photos allowed"
                            )
                        }
                    }
                    .onFailure { exception ->
                        val error = errorHandler.handleException(exception)
                        _uiState.value = _uiState.value.copy(
                            isUploadingPhoto = false,
                            error = errorHandler.createSafeErrorMessage(error)
                        )
                    }
            } catch (e: Exception) {
                val error = errorHandler.handleException(e)
                _uiState.value = _uiState.value.copy(
                    isUploadingPhoto = false,
                    error = errorHandler.createSafeErrorMessage(error)
                )
            }
        }
    }
    
    fun removePhoto(photoUrl: String) {
        val currentPhotos = _uiState.value.photos
        _uiState.value = _uiState.value.copy(
            photos = currentPhotos - photoUrl,
            error = null
        )
    }
    
    fun nextStep() {
        val currentState = _uiState.value
        
        when (currentState.currentStep) {
            0 -> { // Basic info validation
                val nameValidation = inputValidator.validateName(currentState.name)
                if (!nameValidation.isValid) {
                    _uiState.value = currentState.copy(error = nameValidation.errorMessage)
                    return
                }
                
                val ageInt = currentState.age.toIntOrNull() ?: 0
                val ageValidation = inputValidator.validateAge(ageInt)
                if (!ageValidation.isValid) {
                    _uiState.value = currentState.copy(error = ageValidation.errorMessage)
                    return
                }
            }
            1 -> { // Bio validation
                val bioValidation = inputValidator.validateBio(currentState.bio)
                if (!bioValidation.isValid) {
                    _uiState.value = currentState.copy(error = bioValidation.errorMessage)
                    return
                }
            }
            2 -> { // Interests validation
                val interestsValidation = inputValidator.validateInterests(currentState.interests)
                if (!interestsValidation.isValid) {
                    _uiState.value = currentState.copy(error = interestsValidation.errorMessage)
                    return
                }
            }
            3 -> { // Photos validation
                if (currentState.photos.isEmpty()) {
                    _uiState.value = currentState.copy(error = "At least one photo is required")
                    return
                }
            }
        }
        
        if (currentState.currentStep < 3) {
            _uiState.value = currentState.copy(
                currentStep = currentState.currentStep + 1,
                error = null
            )
        } else {
            completeProfile()
        }
    }
    
    fun previousStep() {
        val currentState = _uiState.value
        if (currentState.currentStep > 0) {
            _uiState.value = currentState.copy(
                currentStep = currentState.currentStep - 1,
                error = null
            )
        }
    }
    
    private fun completeProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val userId = authRepository.getCurrentUserId()
                    ?: throw SecurityException("User not authenticated")
                
                val currentState = _uiState.value
                val user = User(
                    uid = userId,
                    name = currentState.name,
                    age = currentState.age.toInt(),
                    gender = currentState.gender,
                    bio = currentState.bio,
                    interests = currentState.interests,
                    photos = currentState.photos,
                    createdAt = System.currentTimeMillis(),
                    lastActive = System.currentTimeMillis()
                )
                
                userRepository.updateUserProfile(user)
                    .onSuccess {
                        preferencesManager.setProfileCreated(userId)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isCompleted = true
                        )
                    }
                    .onFailure { exception ->
                        val error = errorHandler.handleException(exception)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = errorHandler.createSafeErrorMessage(error)
                        )
                    }
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