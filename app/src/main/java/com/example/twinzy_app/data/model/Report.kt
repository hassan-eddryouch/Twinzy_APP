package com.example.twinzy_app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Report(
    val reportId: String = "",
    val reporterId: String = "",
    val reportedUserId: String = "",
    val reason: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable