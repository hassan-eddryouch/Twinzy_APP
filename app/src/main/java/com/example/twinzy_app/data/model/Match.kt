package com.example.twinzy_app.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
@Parcelize
data class Match(
    val matchId: String = "",
    val users: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastMessage: Message? = null,
    val unreadCount: Map<String, Int> = emptyMap()
) : Parcelable