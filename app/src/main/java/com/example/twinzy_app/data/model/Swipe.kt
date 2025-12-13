package com.example.twinzy_app.data.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Swipe(
    var userId: String = "",
    var targetUserId: String = "",
    @get:PropertyName("like") @set:PropertyName("like")
    var isLike: Boolean = false,
    @get:PropertyName("superLike") @set:PropertyName("superLike")
    var isSuperLike: Boolean = false,
    var timestamp: Long = System.currentTimeMillis(),
    var stability: Int = 0
) : Parcelable