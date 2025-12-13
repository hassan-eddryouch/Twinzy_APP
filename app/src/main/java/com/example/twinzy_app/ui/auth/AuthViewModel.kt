package com.example.twinzy_app.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twinzy_app.data.model.User
import com.example.twinzy_app.data.repository.AuthRepository
import com.example.twinzy_app.data.repository.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val needsProfileCompletion: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val verificationId: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val preferencesManager: com.example.twinzy_app.data.manager.PreferencesManager,
    private val inputValidator: com.example.twinzy_app.utils.InputValidator,
    private val errorHandler: com.example.twinzy_app.utils.ErrorHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    val needsCompletion = user.name.isEmpty() || 
                        user.age == 0 || 
                        user.photos.isEmpty()
                    
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = true,
                        user = user,
                        needsProfileCompletion = needsCompletion
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isAuthenticated = false,
                        user = null,
                        needsProfileCompletion = false
                    )
                }
            }
        }
    }
    
    fun refreshUserState() {
        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId()
            if (currentUserId != null) {
                userRepository.getUserById(currentUserId)
                    .onSuccess { user ->
                        if (user != null) {
                            val needsCompletion = user.name.isEmpty() || 
                                user.age == 0 || 
                                user.photos.isEmpty()
                            
                            _uiState.value = _uiState.value.copy(
                                isAuthenticated = true,
                                user = user,
                                needsProfileCompletion = needsCompletion
                            )
                        }
                    }
            }
        }
    }
    
    fun signUpWithEmail(email: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // Client-side validation
                val emailValidation = inputValidator.validateEmail(email)
                if (!emailValidation.isValid) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = emailValidation.errorMessage
                    )
                    return@launch
                }
                
                val passwordValidation = inputValidator.validatePassword(password)
                if (!passwordValidation.isValid) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = passwordValidation.errorMessage
                    )
                    return@launch
                }
                
                val nameValidation = inputValidator.validateName(name)
                if (!nameValidation.isValid) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = nameValidation.errorMessage
                    )
                    return@launch
                }
                
                authRepository.signUpWithEmail(email.trim(), password, name.trim())
                    .onSuccess { user ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            needsProfileCompletion = true,
                            user = user
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
    
    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authRepository.signInWithEmail(email, password)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        needsProfileCompletion = user.name.isEmpty() || user.age == 0 || user.photos.isEmpty(),
                        user = user
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
    
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authRepository.signInWithGoogleToken(idToken)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        needsProfileCompletion = user.age == 0 || user.photos.isEmpty(),
                        user = user
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
    
    fun signInWithPhoneCredential(credential: com.google.firebase.auth.PhoneAuthCredential) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authRepository.signInWithPhoneCredential(credential)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        needsProfileCompletion = user.name.isEmpty() || user.age == 0 || user.photos.isEmpty(),
                        user = user
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
    
    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                    // Auto-verification completed
                }
                
                override fun onVerificationFailed(e: FirebaseException) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                
                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        verificationId = verificationId
                    )
                }
            }
            
            authRepository.sendVerificationCode(phoneNumber, activity, callbacks)
        }
    }
    
    fun signInWithPhone(verificationId: String, code: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authRepository.signInWithPhone(verificationId, code)
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        needsProfileCompletion = true,
                        user = user
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
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            preferencesManager.clearUserData()
            _uiState.value = AuthState()
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun setError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }
    

}