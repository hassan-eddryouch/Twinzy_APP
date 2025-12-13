package com.example.twinzy_app.data.repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.twinzy_app.data.model.*
import com.example.twinzy_app.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override fun observeMessages(matchId: String): Flow<List<Message>> {
        if (matchId.isEmpty()) {
            return flow { emit(emptyList()) }
        }
        
        return callbackFlow {
            try {
                val listener = firestore.collection(Constants.COLLECTION_MATCHES)
                    .document(matchId)
                    .collection(Constants.SUB_COLLECTION_CHAT)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(emptyList())
                            return@addSnapshotListener
                        }

                        val messages = try {
                            snapshot?.documents?.mapNotNull { doc ->
                                try {
                                    doc.toObject(Message::class.java)?.copy(messageId = doc.id)
                                } catch (e: Exception) {
                                    null
                                }
                            } ?: emptyList()
                        } catch (e: Exception) {
                            emptyList()
                        }

                        trySend(messages)
                    }

                awaitClose { 
                    try {
                        listener.remove()
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
            } catch (e: Exception) {
                trySend(emptyList())
                close()
            }
        }
    }

    override suspend fun sendMessage(message: Message): Result<Unit> {
        return try {
            if (message.matchId.isBlank() || message.senderId.isBlank() || message.text.isBlank()) {
                return Result.failure(Exception("Invalid message data"))
            }

            val messageData = mapOf(
                "matchId" to message.matchId,
                "senderId" to message.senderId,
                "receiverId" to message.receiverId,
                "text" to message.text,
                "timestamp" to message.timestamp,
                "messageType" to message.messageType.name,
                "isRead" to false
            )
            
            firestore.collection(Constants.COLLECTION_MATCHES)
                .document(message.matchId)
                .collection(Constants.SUB_COLLECTION_CHAT)
                .add(messageData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markMessageAsRead(matchId: String, messageId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_MATCHES)
                .document(matchId)
                .collection(Constants.SUB_COLLECTION_CHAT)
                .document(messageId)
                .update("isRead", true)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendImage(matchId: String, senderId: String, imageUri: Uri): Result<Unit> =
        suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(imageUri)
                .option("folder", "twinzy/chat")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        if (url != null) {
                            val message = Message(
                                matchId = matchId,
                                senderId = senderId,
                                imageUrl = url,
                                timestamp = System.currentTimeMillis(),
                                messageType = MessageType.IMAGE
                            )

                            firestore.collection(Constants.COLLECTION_MATCHES)
                                .document(matchId)
                                .collection(Constants.SUB_COLLECTION_CHAT)
                                .add(message)

                            continuation.resume(Result.success(Unit))
                        } else {
                            continuation.resume(Result.failure(Exception("Failed to get URL")))
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        continuation.resume(Result.failure(Exception(error.description)))
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                })
                .dispatch()
        }
}