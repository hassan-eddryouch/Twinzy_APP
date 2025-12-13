package com.example.twinzy_app.data.repository

import com.example.twinzy_app.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface DiscoverRepository {
    suspend fun getDiscoverProfiles(userId: String): Result<List<UserProfile>>
    suspend fun swipeLeft(userId: String, targetUserId: String): Result<Boolean>
    suspend fun swipeRight(userId: String, targetUserId: String): Result<Boolean>
    suspend fun superLike(userId: String, targetUserId: String): Result<Boolean>
}

@Singleton
class DiscoverRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository
) : DiscoverRepository {
    
    override suspend fun getDiscoverProfiles(userId: String): Result<List<UserProfile>> {
        return try {
            val currentUser = userRepository.getUserById(userId).getOrThrow()
            val swipedUsers = getSwipedUsers(userId)
            
            val snapshot = firestore.collection("users")
                .whereNotEqualTo("uid", userId)
                .limit(50)
                .get()
                .await()
            
            val profiles = snapshot.documents
                .mapNotNull { it.toObject(User::class.java) }
                .filter { user -> 
                    user.uid !in swipedUsers &&
                    (currentUser?.preferences?.minAge?.let { min -> 
                        currentUser.preferences?.maxAge?.let { max -> 
                            user.age in min..max 
                        } 
                    } ?: true) &&
                    user.name.isNotBlank() &&
                    user.age > 0 &&
                    user.photos.isNotEmpty()
                }
                .map { user -> UserProfile(user) }
                .shuffled()
                .take(10)
            
            Result.success(profiles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun swipeLeft(userId: String, targetUserId: String): Result<Boolean> {
        return try {
            saveSwipe(userId, targetUserId, SwipeAction.DISLIKE)
            Result.success(false) // No match for dislike
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun swipeRight(userId: String, targetUserId: String): Result<Boolean> {
        return try {
            saveSwipe(userId, targetUserId, SwipeAction.LIKE)
            val isMatch = checkForMatch(userId, targetUserId)
            if (isMatch) {
                createMatch(userId, targetUserId)
            }
            Result.success(isMatch)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun superLike(userId: String, targetUserId: String): Result<Boolean> {
        return try {
            saveSwipe(userId, targetUserId, SwipeAction.SUPER_LIKE)
            val isMatch = checkForMatch(userId, targetUserId)
            if (isMatch) {
                createMatch(userId, targetUserId)
            }
            Result.success(isMatch)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun saveSwipe(userId: String, targetUserId: String, action: SwipeAction) {
        val swipe = mapOf(
            "userId" to userId,
            "targetUserId" to targetUserId,
            "action" to action.name,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("swipes").add(swipe).await()
    }
    
    private suspend fun checkForMatch(userId: String, targetUserId: String): Boolean {
        val snapshot = firestore.collection("swipes")
            .whereEqualTo("userId", targetUserId)
            .whereEqualTo("targetUserId", userId)
            .whereIn("action", listOf(SwipeAction.LIKE.name, SwipeAction.SUPER_LIKE.name))
            .get()
            .await()
        return !snapshot.isEmpty
    }
    
    private suspend fun createMatch(userId1: String, userId2: String) {
        val match = mapOf(
            "user1Id" to userId1,
            "user2Id" to userId2,
            "timestamp" to System.currentTimeMillis(),
            "lastMessage" to "",
            "unreadCount" to 0
        )
        firestore.collection("matches").add(match).await()
    }
    
    private suspend fun getSwipedUsers(userId: String): List<String> {
        return try {
            val snapshot = firestore.collection("swipes")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.getString("targetUserId") }
        } catch (e: Exception) {
            emptyList()
        }
    }
}