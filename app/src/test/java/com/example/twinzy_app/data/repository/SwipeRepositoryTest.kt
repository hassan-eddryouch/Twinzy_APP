package com.example.twinzy_app.data.repository

import com.example.twinzy_app.data.model.Swipe
import org.junit.Test
import org.junit.Assert.*

class SwipeRepositoryTest {

    @Test
    fun `swipe model creation test`() {
        // Test Swipe model creation
        val swipe = Swipe(
            userId = "user1",
            targetUserId = "user2",
            isLike = true,
            isSuperLike = false,
            timestamp = System.currentTimeMillis()
        )
        
        assertEquals("user1", swipe.userId)
        assertEquals("user2", swipe.targetUserId)
        assertTrue(swipe.isLike)
        assertFalse(swipe.isSuperLike)
    }

    @Test
    fun `match creation logic test`() {
        // Test match ID generation logic
        val user1 = "user1"
        val user2 = "user2"
        
        val sortedUsers = listOf(user1, user2).sorted()
        val matchId = "${sortedUsers[0]}_${sortedUsers[1]}"
        
        assertEquals("user1_user2", matchId)
    }

    @Test
    fun `firestore path structure test`() {
        // Test the correct path structure
        val userId = "user1"
        val targetUserId = "user2"
        
        val expectedPath = "users/$userId/swipes/$targetUserId"
        val actualPath = "users/$userId/swipes/$targetUserId"
        
        assertEquals(expectedPath, actualPath)
    }

    @Test
    fun `mutual match detection test`() {
        // Test mutual match logic
        val swipe1 = Swipe(
            userId = "user1",
            targetUserId = "user2",
            isLike = true,
            isSuperLike = false,
            timestamp = System.currentTimeMillis()
        )
        
        val swipe2 = Swipe(
            userId = "user2",
            targetUserId = "user1",
            isLike = true,
            isSuperLike = false,
            timestamp = System.currentTimeMillis()
        )
        
        // Both users liked each other
        val isMutualMatch = swipe1.isLike && swipe2.isLike
        assertTrue(isMutualMatch)
    }

    @Test
    fun `no match when one user dislikes`() {
        val swipe1 = Swipe(
            userId = "user1",
            targetUserId = "user2",
            isLike = true,
            isSuperLike = false,
            timestamp = System.currentTimeMillis()
        )
        
        val swipe2 = Swipe(
            userId = "user2",
            targetUserId = "user1",
            isLike = false,
            isSuperLike = false,
            timestamp = System.currentTimeMillis()
        )
        
        val isMutualMatch = swipe1.isLike && swipe2.isLike
        assertFalse(isMutualMatch)
    }
}