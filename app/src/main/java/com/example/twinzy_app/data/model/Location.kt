package com.example.twinzy_app.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
@Parcelize
data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val city: String = "",
    val country: String = ""
) : Parcelable