package com.example.twinzy_app.data.repository

import android.net.Uri
import com.example.twinzy_app.data.model.User
import com.example.twinzy_app.utils.CloudinaryManager
import com.example.twinzy_app.utils.ErrorHandler
import com.example.twinzy_app.utils.InputValidator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val cloudinaryManager: CloudinaryManager,
    private val inputValidator: InputValidator,
    private val errorHandler: ErrorHandler,
    private val auth: FirebaseAuth
) : UserRepository {

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            // Validate user data
            val nameValidation = inputValidator.validateName(user.name)
            if (!nameValidation.isValid) {
                throw IllegalArgumentException(nameValidation.errorMessage)
            }
            
            val ageValidation = inputValidator.validateAge(user.age)
            if (!ageValidation.isValid) {
                throw IllegalArgumentException(ageValidation.errorMessage)
            }
            
            val bioValidation = inputValidator.validateBio(user.bio)
            if (!bioValidation.isValid) {
                throw IllegalArgumentException(bioValidation.errorMessage)
            }
            
            val interestsValidation = inputValidator.validateInterests(user.interests)
            if (!interestsValidation.isValid) {
                throw IllegalArgumentException(interestsValidation.errorMessage)
            }
            
            // Sanitize user data
            val sanitizedUser = user.copy(
                name = inputValidator.sanitizeInput(user.name),
                bio = inputValidator.sanitizeInput(user.bio),
                interests = user.interests.map { inputValidator.sanitizeInput(it) }
            )
            
            // Update in Firestore
            firestore.collection("users")
                .document(user.uid)
                .set(sanitizedUser)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            val error = errorHandler.handleException(e)
            Result.failure(Exception(errorHandler.createSafeErrorMessage(error)))
        }
    }

    override suspend fun uploadProfileImage(uri: Uri, userId: String): Result<String> {
        return try {
            // Validate user is authenticated
            val currentUserId = auth.currentUser?.uid
            if (currentUserId != userId) {
                throw SecurityException("Unauthorized image upload")
            }
            
            cloudinaryManager.uploadImage(uri, "profile_images", userId)
        } catch (e: Exception) {
            val error = errorHandler.handleException(e)
            Result.failure(Exception(errorHandler.createSafeErrorMessage(error)))
        }
    }

    override suspend fun getUserById(userId: String): Result<User?> {
        return try {
            // Sanitize userId to prevent injection
            val sanitizedUserId = userId.replace(Regex("[^a-zA-Z0-9]"), "")
            if (sanitizedUserId != userId || userId.length > 128) {
                throw IllegalArgumentException("Invalid user ID")
            }
            
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()
                
            val user = document.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            val error = errorHandler.handleException(e)
            Result.failure(Exception(errorHandler.createSafeErrorMessage(error)))
        }
    }

    override fun getUserFlow(userId: String): Flow<User?> = callbackFlow {
        try {
            // Validate userId
            val sanitizedUserId = userId.replace(Regex("[^a-zA-Z0-9]"), "")
            if (sanitizedUserId != userId || userId.length > 128) {
                throw IllegalArgumentException("Invalid user ID")
            }
            
            val listener = firestore.collection("users")
                .document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    
                    val user = snapshot?.toObject(User::class.java)
                    trySend(user)
                }
                
            awaitClose { listener.remove() }
        } catch (e: Exception) {
            close(e)
        }
    }

    override suspend fun updateUserLocation(userId: String, latitude: Double, longitude: Double): Result<Unit> {
        return try {
            // Validate coordinates
            if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
                throw IllegalArgumentException("Invalid coordinates")
            }
            
            // Validate user authorization
            val currentUserId = auth.currentUser?.uid
            if (currentUserId != userId) {
                throw SecurityException("Unauthorized location update")
            }
            
            firestore.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "location.latitude" to latitude,
                        "location.longitude" to longitude,
                        "lastActive" to System.currentTimeMillis()
                    )
                )
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            val error = errorHandler.handleException(e)
            Result.failure(Exception(errorHandler.createSafeErrorMessage(error)))
        }
    }

    override suspend fun updateOnlineStatus(userId: String, isOnline: Boolean): Result<Unit> {
        return try {
            // Validate user authorization
            val currentUserId = auth.currentUser?.uid
            if (currentUserId != userId) {
                throw SecurityException("Unauthorized status update")
            }
            
            firestore.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "isOnline" to isOnline,
                        "lastActive" to System.currentTimeMillis()
                    )
                )
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            val error = errorHandler.handleException(e)
            Result.failure(Exception(errorHandler.createSafeErrorMessage(error)))
        }
    }
}