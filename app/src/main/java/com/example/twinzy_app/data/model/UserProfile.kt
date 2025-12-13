package com.example.twinzy_app.data.model

data class UserProfile(
    val user: User,
    val distance: Double? = null,
    val commonInterests: List<String> = emptyList()
)