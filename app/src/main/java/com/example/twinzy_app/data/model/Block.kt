package com.example.twinzy_app.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Block(
    val blockId: String = "",
    val blockerId: String = "",
    val blockedUserId: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable