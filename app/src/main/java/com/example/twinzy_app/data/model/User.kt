package com.example.twinzy_app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val age: Int = 0,
    val gender: Gender = Gender.MALE,
    val bio: String = "",
    val photos: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val fcmToken: String = "",
    val location: Location? = null,
    val preferences: Preferences = Preferences(),
    val createdAt: Long = 0L,
    val lastActive: Long = 0L,
    val isOnline: Boolean = false
) : Parcelable

enum class Gender {
    MALE, FEMALE, NON_BINARY, OTHER
}