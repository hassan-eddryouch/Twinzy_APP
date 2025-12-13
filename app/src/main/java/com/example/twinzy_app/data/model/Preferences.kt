package com.example.twinzy_app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Preferences(
    val genderPreference: List<Gender> = listOf(Gender.NON_BINARY),
    val minAge: Int = 18,
    val maxAge: Int = 100,
    val maxDistance: Int = 50 // in kilometers
) : Parcelable
