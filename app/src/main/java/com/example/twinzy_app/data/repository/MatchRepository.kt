package com.example.twinzy_app.data.repository

import com.example.twinzy_app.data.model.Match
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface MatchRepository {
    fun observeMatches(userId: String): Flow<List<Match>>
    suspend fun getMatches(userId: String): Result<List<Match>>
    suspend fun getMatch(matchId: String): Result<Match>
    suspend fun unmatch(matchId: String): Result<Unit>
}

class MatchRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val messaging: FirebaseMessaging
) : MatchRepository {

    override fun observeMatches(userId: String): Flow<List<Match>> = callbackFlow {
        val listener = firestore.collection("matches")
            .whereArrayContains("users", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val matches = snapshot?.documents?.mapNotNull {
                    it.toObject(Match::class.java)
                } ?: emptyList()

                trySend(matches)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getMatches(userId: String): Result<List<Match>> {
        return try {
            val snapshot = firestore.collection("matches")
                .whereArrayContains("users", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val matches = snapshot.documents.mapNotNull {
                it.toObject(Match::class.java)
            }

            Result.success(matches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMatch(matchId: String): Result<Match> {
        return try {
            val document = firestore.collection("matches")
                .document(matchId)
                .get()
                .await()

            val match = document.toObject(Match::class.java)
                ?: throw Exception("Match not found")

            Result.success(match)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unmatch(matchId: String): Result<Unit> {
        return try {
            firestore.collection("matches")
                .document(matchId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}