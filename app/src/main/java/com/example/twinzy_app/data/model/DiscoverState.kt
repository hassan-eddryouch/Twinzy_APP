package com.example.twinzy_app.data.model

sealed class DiscoverState {
    object Loading : DiscoverState()
    data class Success(val profiles: List<UserProfile>) : DiscoverState()
    data class Error(val message: String) : DiscoverState()
    object Empty : DiscoverState()
}