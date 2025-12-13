package com.example.twinzy_app.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.twinzy_app.config.SecurityConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class CloudinaryManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val securityConfig: SecurityConfig,
    private val errorHandler: ErrorHandler
) {
    companion object {
        private const val TAG = "CloudinaryManager"
        // 5 Minutes (300,000 ms) timeout bach ntehnnaw mn TimeoutException
        private const val UPLOAD_TIMEOUT = 300000L
    }

    private var isInitialized = false

    init {
        initializeCloudinary()
    }

    private fun initializeCloudinary() {
        try {
            MediaManager.get()
            isInitialized = true
        } catch (e: Exception) {
            try {
                if (securityConfig.validateConfiguration()) {
                    val config = securityConfig.getCloudinaryConfig()
                    MediaManager.init(context, config)
                    isInitialized = true
                    Log.d(TAG, "Cloudinary initialized successfully")
                } else {
                    Log.e(TAG, "Invalid Cloudinary configuration")
                }
            } catch (initException: Exception) {
                Log.e(TAG, "Failed to initialize Cloudinary", initException)
                isInitialized = false
            }
        }
    }

    suspend fun uploadImage(
        uri: Uri,
        folder: String = "twinzy",
        userId: String
    ): Result<String> {
        return try {
            if (!isInitialized) {
                initializeCloudinary()
                if (!isInitialized) {
                    throw IllegalStateException("Cloudinary not initialized. Check API Keys.")
                }
            }

            validateImageUri(uri)

            // Timeout dial 5 minutes
            val url = withTimeout(UPLOAD_TIMEOUT) {
                suspendCancellableCoroutine { continuation ->
                    val sanitizedFolder = sanitizeFolder(folder)
                    val publicId = generateSecurePublicId(userId)

                    Log.d(TAG, "Starting upload for user: $userId")

                    MediaManager.get()
                        .upload(uri)
                        .option("folder", sanitizedFolder)
                        .option("public_id", publicId)
                        .option("resource_type", "image")
                        // --- FIX 502 BAD GATEWAY ---
                        // 1. Hiyedna quality w fetch_format bach tkon request khfifa
                        // 2. Rddina secure = false bach l'emulator maydirch mochkil m3a SSL
                        .option("secure", false)
                        .callback(object : UploadCallback {
                            override fun onStart(requestId: String) {
                                Log.d(TAG, "Upload started: $requestId")
                            }

                            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            }

                            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                                Log.d(TAG, "Upload Success: $requestId")
                                try {
                                    // Hitach secure=false, Cloudinary kayrje3 lien HTTP
                                    val url = resultData["url"] as? String

                                    if (url != null) {
                                        // Hna kanbedlo HTTP b HTTPS manuellemnt
                                        // Bach tswera tban mziyan f Android (li kay-bloqué http)
                                        val secureUrl = url.replace("http://", "https://")
                                        continuation.resume(secureUrl)
                                    } else {
                                        continuation.resumeWithException(SecurityException("Invalid URL returned"))
                                    }
                                } catch (e: Exception) {
                                    continuation.resumeWithException(e)
                                }
                            }

                            override fun onError(requestId: String, error: ErrorInfo) {
                                Log.e(TAG, "Upload Error: ${error.description} code: ${error.code}")
                                continuation.resumeWithException(Exception("Upload failed: ${error.description}"))
                            }

                            override fun onReschedule(requestId: String, error: ErrorInfo) {
                                continuation.resumeWithException(Exception("Upload rescheduled: ${error.description}"))
                            }
                        })
                        .dispatch()
                }
            }

            Result.success(url)
        } catch (e: Exception) {
            Log.e(TAG, "Upload failed exception", e)
            Result.failure(errorHandler.handleException(e).let {
                Exception(errorHandler.createSafeErrorMessage(it))
            })
        }
    }

    private fun validateImageUri(uri: Uri) {
        if (uri.toString().isEmpty()) {
            throw IllegalArgumentException("Invalid image URI")
        }
    }

    private fun sanitizeFolder(folder: String): String {
        return folder.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(50).lowercase()
    }

    private fun generateSecurePublicId(userId: String): String {
        val timestamp = System.currentTimeMillis()
        val sanitizedUserId = userId.replace(Regex("[^a-zA-Z0-9]"), "")
        return "user_${sanitizedUserId}_${timestamp}"
    }
}