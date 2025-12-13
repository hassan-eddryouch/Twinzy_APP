package com.example.twinzy_app.data.repository

import android.net.Uri
import com.example.twinzy_app.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun uploadProfileImage(uri: Uri, userId: String): Result<String>
    suspend fun getUserById(userId: String): Result<User?>
    fun getUserFlow(userId: String): Flow<User?>
    suspend fun updateUserLocation(userId: String, latitude: Double, longitude: Double): Result<Unit>
    suspend fun updateOnlineStatus(userId: String, isOnline: Boolean): Result<Unit>
}