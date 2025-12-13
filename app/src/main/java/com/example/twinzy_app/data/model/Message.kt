package com.example.twinzy_app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val messageId: String = "",
    val matchId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val messageType: MessageType = MessageType.TEXT
) : Parcelable

enum class MessageType {
    TEXT,
    IMAGE,
    SYSTEM
}