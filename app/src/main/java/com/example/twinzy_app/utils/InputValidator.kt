package com.example.twinzy_app.utils

import android.util.Patterns
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InputValidator @Inject constructor() {
    
    companion object {
        private val EMAIL_PATTERN = Patterns.EMAIL_ADDRESS
        private val PHONE_PATTERN = Pattern.compile("^[+]?[1-9]\\d{1,14}$")
        private val NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{2,50}$")
        private val PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$")
        private val BIO_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s.,!?'-]{0,500}$")
        
        // Firestore injection prevention patterns
        private val FIRESTORE_INJECTION_PATTERNS = listOf(
            Pattern.compile(".*[{}\\[\\]\"'`].*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\$.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\.\\..*", Pattern.CASE_INSENSITIVE)
        )
    }
    
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )
    
    fun validateEmail(email: String): ValidationResult {
        val sanitized = sanitizeInput(email)
        return when {
            sanitized.isBlank() -> ValidationResult(false, "Email is required")
            sanitized.length > 254 -> ValidationResult(false, "Email is too long")
            !EMAIL_PATTERN.matcher(sanitized).matches() -> ValidationResult(false, "Invalid email format")
            containsFirestoreInjection(sanitized) -> ValidationResult(false, "Invalid characters in email")
            else -> ValidationResult(true)
        }
    }
    
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult(false, "Password is required")
            password.length < 8 -> ValidationResult(false, "Password must be at least 8 characters")
            password.length > 128 -> ValidationResult(false, "Password is too long")
            !PASSWORD_PATTERN.matcher(password).matches() -> 
                ValidationResult(false, "Password must contain uppercase, lowercase, number and special character")
            else -> ValidationResult(true)
        }
    }
    
    fun validateName(name: String): ValidationResult {
        val sanitized = sanitizeInput(name)
        return when {
            sanitized.isBlank() -> ValidationResult(false, "Name is required")
            sanitized.length < 2 -> ValidationResult(false, "Name must be at least 2 characters")
            sanitized.length > 50 -> ValidationResult(false, "Name is too long")
            !NAME_PATTERN.matcher(sanitized).matches() -> ValidationResult(false, "Name contains invalid characters")
            containsFirestoreInjection(sanitized) -> ValidationResult(false, "Invalid characters in name")
            else -> ValidationResult(true)
        }
    }
    
    fun validatePhone(phone: String): ValidationResult {
        val sanitized = sanitizePhoneNumber(phone)
        return when {
            sanitized.isBlank() -> ValidationResult(false, "Phone number is required")
            !PHONE_PATTERN.matcher(sanitized).matches() -> ValidationResult(false, "Invalid phone number format")
            containsFirestoreInjection(sanitized) -> ValidationResult(false, "Invalid characters in phone number")
            else -> ValidationResult(true)
        }
    }
    
    fun validateAge(age: Int): ValidationResult {
        return when {
            age < 18 -> ValidationResult(false, "Must be at least 18 years old")
            age > 100 -> ValidationResult(false, "Invalid age")
            else -> ValidationResult(true)
        }
    }
    
    fun validateBio(bio: String): ValidationResult {
        val sanitized = sanitizeInput(bio)
        return when {
            sanitized.length > 500 -> ValidationResult(false, "Bio is too long (max 500 characters)")
            !BIO_PATTERN.matcher(sanitized).matches() -> ValidationResult(false, "Bio contains invalid characters")
            containsFirestoreInjection(sanitized) -> ValidationResult(false, "Invalid characters in bio")
            else -> ValidationResult(true)
        }
    }
    
    fun sanitizeInput(input: String): String {
        return input.trim()
            .replace(Regex("[\\x00-\\x1F\\x7F]"), "") // Remove control characters
            .replace(Regex("\\s+"), " ") // Normalize whitespace
    }
    
    fun sanitizePhoneNumber(phone: String): String {
        return phone.replace(Regex("[^+\\d]"), "")
    }
    
    private fun containsFirestoreInjection(input: String): Boolean {
        return FIRESTORE_INJECTION_PATTERNS.any { pattern ->
            pattern.matcher(input).matches()
        }
    }
    
    fun validateInterests(interests: List<String>): ValidationResult {
        return when {
            interests.isEmpty() -> ValidationResult(false, "At least one interest is required")
            interests.size > 10 -> ValidationResult(false, "Too many interests (max 10)")
            interests.any { it.length > 30 } -> ValidationResult(false, "Interest name too long")
            interests.any { containsFirestoreInjection(it) } -> ValidationResult(false, "Invalid characters in interests")
            else -> ValidationResult(true)
        }
    }
}