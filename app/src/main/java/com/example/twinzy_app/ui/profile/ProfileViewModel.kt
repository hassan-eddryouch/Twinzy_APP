package com.example.twinzy_app.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twinzy_app.data.model.Gender
import com.example.twinzy_app.data.model.User
import com.example.twinzy_app.data.repository.UserRepository
import com.example.twinzy_app.utils.CloudinaryManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val uploadedImageUrl: String? = null,
    val currentUser: User? = null,
    val error: String? = null,
    val isProfileComplete: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val cloudinaryManager: CloudinaryManager,
    private val preferencesManager: com.example.twinzy_app.data.manager.PreferencesManager,
    private val auth: FirebaseAuth
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadCurrentUser()
    }
    
    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            _uiState.value = _uiState.value.copy(isUploading = true, error = null)
            
            cloudinaryManager.uploadImage(uri, "twinzy/profiles", userId)
                .onSuccess { imageUrl ->
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        uploadedImageUrl = imageUrl
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        error = exception.message
                    )
                }
        }
    }
    
    fun loadCurrentUser() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            userRepository.getUserById(userId)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = user,
                        isProfileComplete = user?.name?.isNotBlank() == true && (user?.age ?: 0) > 0 && (user?.photos?.isNotEmpty() == true)
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
    
    fun updateProfile(
        name: String,
        bio: String = "",
        age: Int,
        gender: Gender,
        interests: List<String>
    ) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val currentUser = _uiState.value.currentUser
            val photos = currentUser?.photos ?: emptyList()
            
            val updatedUser = User(
                uid = userId,
                name = name,
                bio = bio,
                age = age,
                gender = gender,
                interests = interests,
                photos = photos,
                email = currentUser?.email ?: "",
                phoneNumber = currentUser?.phoneNumber ?: "",
                fcmToken = currentUser?.fcmToken ?: "",
                createdAt = currentUser?.createdAt ?: System.currentTimeMillis(),
                lastActive = System.currentTimeMillis(),
                isOnline = true
            )
            
            userRepository.updateUserProfile(updatedUser)
                .onSuccess {
                    viewModelScope.launch {
                        preferencesManager.setProfileCreated(userId)
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = updatedUser,
                        isProfileComplete = true
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
    
    fun createProfile(
        name: String,
        age: Int,
        gender: Gender,
        bio: String = "",
        interests: List<String>,
        photoUrl: String? = null
    ) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val photos = if (photoUrl != null) listOf(photoUrl) else emptyList()
            
            val updatedUser = User(
                uid = userId,
                name = name,
                age = age,
                gender = gender,
                bio = bio,
                interests = interests,
                photos = photos,
                email = _uiState.value.currentUser?.email ?: "",
                phoneNumber = _uiState.value.currentUser?.phoneNumber ?: "",
                fcmToken = _uiState.value.currentUser?.fcmToken ?: "",
                createdAt = _uiState.value.currentUser?.createdAt ?: System.currentTimeMillis(),
                lastActive = System.currentTimeMillis(),
                isOnline = true
            )
            
            userRepository.updateUserProfile(updatedUser)
                .onSuccess {
                    // Mark onboarding and profile as complete
                    preferencesManager.setOnboardingCompleted()
                    preferencesManager.setProfileCreated(userId)
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentUser = updatedUser,
                        isProfileComplete = true
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
    
    fun addPhotoToProfile(photoUrl: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.currentUser ?: return@launch
            val updatedPhotos = currentUser.photos + photoUrl
            
            val updatedUser = currentUser.copy(photos = updatedPhotos)
            
            userRepository.updateUserProfile(updatedUser)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        currentUser = updatedUser
                    )
                }
        }
    }
    
    fun removePhotoFromProfile(photoUrl: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.currentUser ?: return@launch
            val updatedPhotos = currentUser.photos.filter { it != photoUrl }
            
            val updatedUser = currentUser.copy(photos = updatedPhotos)
            
            userRepository.updateUserProfile(updatedUser)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        currentUser = updatedUser
                    )
                }
        }
    }
}
