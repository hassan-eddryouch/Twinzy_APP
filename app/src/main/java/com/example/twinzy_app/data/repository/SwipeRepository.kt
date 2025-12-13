package com.example.twinzy_app.data.repository

import com.example.twinzy_app.data.local.SwipeHistoryEntity
import com.example.twinzy_app.data.local.TwinzyDatabase
import com.example.twinzy_app.data.model.Match
import com.example.twinzy_app.data.model.Swipe
import com.example.twinzy_app.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

interface SwipeRepository {
    suspend fun swipeUser(swipe: Swipe): Result<Match?>
    suspend fun getSwipedUserIds(userId: String): Result<List<String>>
}

class SwipeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val database: TwinzyDatabase
) : SwipeRepository {

    override suspend fun swipeUser(swipe: Swipe): Result<Match?> {
        return try {
            // 1. Save swipe to Firestore: users/{userId}/swipes/{targetUserId}
            firestore.collection(Constants.COLLECTION_USERS)
                .document(swipe.userId)
                .collection(Constants.COLLECTION_SWIPES)
                .document(swipe.targetUserId)
                .set(swipe)
                .await()

            // 2. Save to Room (Local Cache)
            val swipeEntity = SwipeHistoryEntity(
                userId = swipe.userId,
                targetUserId = swipe.targetUserId,
                isLike = swipe.isLike,
                isSuperLike = swipe.isSuperLike,
                timestamp = swipe.timestamp
            )
            database.swipeHistoryDao().insertSwipe(swipeEntity)

            // 3. Check for mutual match if it's a like
            if (swipe.isLike) {
                // Check reverse swipe: users/{targetUserId}/swipes/{userId}
                val reverseSwipeSnapshot = firestore.collection(Constants.COLLECTION_USERS)
                    .document(swipe.targetUserId)
                    .collection(Constants.COLLECTION_SWIPES)
                    .document(swipe.userId)
                    .get()
                    .await()

                if (reverseSwipeSnapshot.exists()) {
                    val reverseSwipe = reverseSwipeSnapshot.toObject(Swipe::class.java)

                    if (reverseSwipe?.isLike == true) {
                        val match = createMatch(swipe.userId, swipe.targetUserId)
                        return Result.success(match)
                    }
                }
            }

            Result.success(null)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSwipedUserIds(userId: String): Result<List<String>> {
        // Cette fonction utilise la base de données locale (Room) pour la rapidité
        return try {
            val swipedIds = database.swipeHistoryDao().getSwipedUserIds(userId)
            Result.success(swipedIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun createMatch(user1Id: String, user2Id: String): Match {
        // 1. Generate Match ID (Sorted to ensure uniqueness)
        val sortedUsers = listOf(user1Id, user2Id).sorted()
        val matchId = "${sortedUsers[0]}_${sortedUsers[1]}"

        val match = Match(
            matchId = matchId,
            users = sortedUsers,
            createdAt = System.currentTimeMillis(),
            unreadCount = mapOf(user1Id to 0, user2Id to 0)
        )

        // 2. Save Match Document in the dedicated 'matches' collection
        firestore.collection(Constants.COLLECTION_MATCHES)
            .document(matchId)
            .set(match)
            .await()

        return match
    }
}