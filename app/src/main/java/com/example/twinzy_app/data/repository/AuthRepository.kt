package com.example.twinzy_app.data.repository

import android.app.Activity
import com.example.twinzy_app.data.model.User
import com.example.twinzy_app.utils.ErrorHandler
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

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<User>
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User>
    suspend fun signInWithGoogleToken(idToken: String): Result<User>
    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<User>
    suspend fun signInWithPhone(verificationId: String, code: String): Result<User>
    suspend fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )
    suspend fun signOut()
    suspend fun deleteAccount(): Result<Unit>
    fun getCurrentUserId(): String?
}

