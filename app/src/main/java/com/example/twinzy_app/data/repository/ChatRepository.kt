package com.example.twinzy_app.data.repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.twinzy_app.data.local.TwinzyDatabase
import com.example.twinzy_app.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.*

interface ChatRepository {
    fun observeMessages(matchId: String): Flow<List<Message>>
    fun observeLastMessage(matchId: String): Flow<Message?>
    suspend fun sendMessage(message: Message): Result<Unit>
    suspend fun markMessageAsRead(matchId: String, messageId: String): Result<Unit>
    suspend fun sendImage(matchId: String, senderId: String, imageUri: Uri): Result<Unit>
}

