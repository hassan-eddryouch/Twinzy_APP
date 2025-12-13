package com.example.twinzy_app.utils

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor() {
    
    companion object {
        private const val TAG = "TwinzyErrorHandler"
    }
    
    sealed class TwinzyError(val message: String, val code: String) {
        object NetworkError : TwinzyError("Network connection failed", "NETWORK_ERROR")
        object AuthenticationError : TwinzyError("Authentication failed", "AUTH_ERROR")
        object ValidationError : TwinzyError("Invalid input data", "VALIDATION_ERROR")
        object PermissionError : TwinzyError("Permission denied", "PERMISSION_ERROR")
        object ServerError : TwinzyError("Server error occurred", "SERVER_ERROR")
        object UnknownError : TwinzyError("An unexpected error occurred", "UNKNOWN_ERROR")
        
        class CustomError(message: String, code: String) : TwinzyError(message, code)
    }
    
    fun handleException(exception: Throwable): TwinzyError {
        Log.e(TAG, "Handling exception: ${exception.javaClass.simpleName}", exception)
        
        return when (exception) {
            is FirebaseAuthException -> handleAuthException(exception)
            is FirebaseFirestoreException -> handleFirestoreException(exception)
            is FirebaseException -> TwinzyError.ServerError
            is SecurityException -> TwinzyError.PermissionError
            is IllegalArgumentException -> TwinzyError.ValidationError
            else -> {
                if (isNetworkException(exception)) {
                    TwinzyError.NetworkError
                } else {
                    TwinzyError.UnknownError
                }
            }
        }
    }
    
    private fun handleAuthException(exception: FirebaseAuthException): TwinzyError {
        return when (exception.errorCode) {
            "ERROR_INVALID_EMAIL" -> TwinzyError.CustomError("Invalid email address", "INVALID_EMAIL")
            "ERROR_WRONG_PASSWORD" -> TwinzyError.CustomError("Incorrect password", "WRONG_PASSWORD")
            "ERROR_USER_NOT_FOUND" -> TwinzyError.CustomError("No account found with this email", "USER_NOT_FOUND")
            "ERROR_USER_DISABLED" -> TwinzyError.CustomError("Account has been disabled", "USER_DISABLED")
            "ERROR_TOO_MANY_REQUESTS" -> TwinzyError.CustomError("Too many attempts. Try again later", "TOO_MANY_REQUESTS")
            "ERROR_EMAIL_ALREADY_IN_USE" -> TwinzyError.CustomError("Email already registered", "EMAIL_IN_USE")
            "ERROR_WEAK_PASSWORD" -> TwinzyError.CustomError("Password is too weak", "WEAK_PASSWORD")
            "ERROR_INVALID_PHONE_NUMBER" -> TwinzyError.CustomError("Invalid phone number", "INVALID_PHONE")
            "ERROR_INVALID_VERIFICATION_CODE" -> TwinzyError.CustomError("Invalid verification code", "INVALID_CODE")
            else -> TwinzyError.AuthenticationError
        }
    }
    
    private fun handleFirestoreException(exception: FirebaseFirestoreException): TwinzyError {
        return when (exception.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> TwinzyError.PermissionError
            FirebaseFirestoreException.Code.UNAVAILABLE -> TwinzyError.NetworkError
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> TwinzyError.NetworkError
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> TwinzyError.ServerError
            FirebaseFirestoreException.Code.INVALID_ARGUMENT -> TwinzyError.ValidationError
            else -> TwinzyError.ServerError
        }
    }
    
    private fun isNetworkException(exception: Throwable): Boolean {
        val message = exception.message?.lowercase() ?: ""
        return message.contains("network") || 
               message.contains("connection") || 
               message.contains("timeout") ||
               message.contains("unreachable")
    }
    
    fun logError(error: TwinzyError, context: String = "") {
        Log.e(TAG, "[$context] ${error.code}: ${error.message}")
    }
    
    fun createSafeErrorMessage(error: TwinzyError): String {
        // Return user-friendly messages without exposing internal details
        return when (error) {
            is TwinzyError.NetworkError -> "Please check your internet connection and try again"
            is TwinzyError.AuthenticationError -> "Authentication failed. Please try again"
            is TwinzyError.ValidationError -> "Please check your input and try again"
            is TwinzyError.PermissionError -> "You don't have permission to perform this action"
            is TwinzyError.ServerError -> "Server is temporarily unavailable. Please try again later"
            is TwinzyError.CustomError -> error.message
            else -> "Something went wrong. Please try again"
        }
    }
}