package com.example.twinzy_app.data.repository

import android.app.Activity
import com.example.twinzy_app.data.model.User
import com.example.twinzy_app.utils.Constants
import com.example.twinzy_app.utils.ErrorHandler
import com.example.twinzy_app.utils.isValidEmail
import com.example.twinzy_app.utils.isValidPassword
import com.example.twinzy_app.utils.InputValidator
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val messaging: FirebaseMessaging,
    private val inputValidator: InputValidator,
    private val errorHandler: ErrorHandler
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        val user = document.toObject(User::class.java)
                        trySend(user)
                    }
                    .addOnFailureListener {
                        trySend(null)
                    }
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User> {
        return try {
            // Validate inputs using extension functions
            if (!email.isValidEmail()) {
                throw IllegalArgumentException("Invalid email format")
            }
            
            if (!password.isValidPassword()) {
                throw IllegalArgumentException("Password must be at least 6 characters with letters and numbers")
            }
            
            if (name.length < Constants.MIN_NAME_LENGTH) {
                throw IllegalArgumentException("Name must be at least ${Constants.MIN_NAME_LENGTH} characters")
            }

            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = result.user ?: throw Exception("User creation failed")

            val fcmToken = try {
                messaging.token.await()
            } catch (e: Exception) {
                ""
            }

            val sanitizedName = inputValidator.sanitizeInput(name)
            val user = User(
                uid = firebaseUser.uid,
                name = sanitizedName,
                email = email.trim().lowercase(),
                fcmToken = fcmToken,
                createdAt = System.currentTimeMillis(),
                lastActive = System.currentTimeMillis()
            )

            firestore.collection(Constants.COLLECTION_USERS)
                .document(firebaseUser.uid)
                .set(user)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            when (e) {
                is IllegalArgumentException -> Result.failure(e)
                is FirebaseAuthWeakPasswordException -> Result.failure(Exception("Password is too weak. Use at least 6 characters with letters and numbers."))
                is FirebaseAuthInvalidCredentialsException -> Result.failure(Exception("Invalid email format."))
                is FirebaseAuthUserCollisionException -> Result.failure(Exception("An account with this email already exists."))
                else -> {
                    val error = errorHandler.handleException(e)
                    Result.failure(Exception("Please check your input and try again"))
                }
            }
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Sign in failed")

            val fcmToken = try {
                messaging.token.await()
            } catch (e: Exception) {
                ""
            }

            // Update FCM token and last active
            firestore.collection(Constants.COLLECTION_USERS)
                .document(firebaseUser.uid)
                .update(
                    mapOf(
                        "fcmToken" to fcmToken,
                        "lastActive" to System.currentTimeMillis(),
                        "isOnline" to true
                    )
                ).await()

            val document = firestore.collection(Constants.COLLECTION_USERS)
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = document.toObject(User::class.java)
                ?: throw Exception("User not found")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Google sign in failed")

            val fcmToken = try {
                messaging.token.await()
            } catch (e: Exception) {
                ""
            }

            // Check if user exists
            val userDoc = firestore.collection(Constants.COLLECTION_USERS)
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = if (userDoc.exists()) {
                // Update existing user
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .update(
                        mapOf(
                            "fcmToken" to fcmToken,
                            "lastActive" to System.currentTimeMillis(),
                            "isOnline" to true
                        )
                    ).await()
                userDoc.toObject(User::class.java)!!
            } else {
                // Create new user
                val newUser = User(
                    uid = firebaseUser.uid,
                    name = account.displayName ?: "",
                    email = account.email ?: "",
                    fcmToken = fcmToken,
                    createdAt = System.currentTimeMillis(),
                    lastActive = System.currentTimeMillis()
                )
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .set(newUser)
                    .await()
                newUser
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithPhone(verificationId: String, code: String): Result<User> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Phone sign in failed")

            val fcmToken = try {
                messaging.token.await()
            } catch (e: Exception) {
                ""
            }

            // Check if user exists
            val userDoc = firestore.collection(Constants.COLLECTION_USERS)
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = if (userDoc.exists()) {
                // Update existing user
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .update(
                        mapOf(
                            "fcmToken" to fcmToken,
                            "lastActive" to System.currentTimeMillis(),
                            "isOnline" to true
                        )
                    ).await()
                userDoc.toObject(User::class.java)!!
            } else {
                // Create new user
                val newUser = User(
                    uid = firebaseUser.uid,
                    phoneNumber = firebaseUser.phoneNumber ?: "",
                    fcmToken = fcmToken,
                    createdAt = System.currentTimeMillis(),
                    lastActive = System.currentTimeMillis()
                )
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .set(newUser)
                    .await()
                newUser
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        // Validate phone number
        inputValidator.validatePhone(phoneNumber)
        
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun signOut() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .update("isOnline", false)
                .await()
        }
        auth.signOut()
    }

    override suspend fun signInWithGoogleToken(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Google sign in failed")

            val fcmToken = try {
                messaging.token.await()
            } catch (e: Exception) {
                ""
            }

            // Check if user exists
            val userDoc = firestore.collection(Constants.COLLECTION_USERS)
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = if (userDoc.exists()) {
                // Update existing user
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .update(
                        mapOf(
                            "fcmToken" to fcmToken,
                            "lastActive" to System.currentTimeMillis(),
                            "isOnline" to true
                        )
                    ).await()
                userDoc.toObject(User::class.java)!!
            } else {
                // Create new user
                val newUser = User(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    fcmToken = fcmToken,
                    createdAt = System.currentTimeMillis(),
                    lastActive = System.currentTimeMillis()
                )
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .set(newUser)
                    .await()
                newUser
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<User> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Phone sign in failed")

            val fcmToken = try {
                messaging.token.await()
            } catch (e: Exception) {
                ""
            }

            // Check if user exists
            val userDoc = firestore.collection(Constants.COLLECTION_USERS)
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = if (userDoc.exists()) {
                // Update existing user
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .update(
                        mapOf(
                            "fcmToken" to fcmToken,
                            "lastActive" to System.currentTimeMillis(),
                            "isOnline" to true
                        )
                    ).await()
                userDoc.toObject(User::class.java)!!
            } else {
                // Create new user
                val newUser = User(
                    uid = firebaseUser.uid,
                    phoneNumber = firebaseUser.phoneNumber ?: "",
                    fcmToken = fcmToken,
                    createdAt = System.currentTimeMillis(),
                    lastActive = System.currentTimeMillis()
                )
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(firebaseUser.uid)
                    .set(newUser)
                    .await()
                newUser
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("No user signed in")

            // Delete user data from Firestore
            firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .delete()
                .await()

            // Delete authentication account
            auth.currentUser?.delete()?.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUserId(): String? = auth.currentUser?.uid
}