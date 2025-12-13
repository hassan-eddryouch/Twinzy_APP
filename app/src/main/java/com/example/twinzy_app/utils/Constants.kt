package com.example.twinzy_app.utils

object Constants {
    // Firestore Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_MATCHES = "matches"
    const val COLLECTION_MESSAGES = "messages"
    const val SUB_COLLECTION_CHAT = "chat"
    const val COLLECTION_SWIPES = "swipes"
    const val COLLECTION_REPORTS = "reports"
    const val COLLECTION_BLOCKS = "blocks"

    // Storage Paths
    const val STORAGE_PROFILE_IMAGES = "twinzy/profiles"
    const val STORAGE_CHAT_IMAGES = "twinzy/chat"

    // Request Codes & Permissions
    const val PERMISSION_REQUEST_CODE_LOCATION = 1001
    const val PERMISSION_REQUEST_CODE_NOTIFICATION = 1002

    // Pagination
    const val PAGE_SIZE_USERS = 20
    const val PAGE_SIZE_MESSAGES = 50

    // Shared Preferences
    const val PREF_NAME = "twinzy_preferences"
    const val PREF_KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    const val PREF_KEY_LAST_LOCATION_LAT = "last_lat"
    const val PREF_KEY_LAST_LOCATION_LNG = "last_lng"

    // Timeouts
    const val NETWORK_TIMEOUT = 30000L // 30 seconds
    
    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_NAME_LENGTH = 2
    const val MAX_BIO_LENGTH = 500

    // Date Formats
    const val DATE_FORMAT_DOB = "dd/MM/yyyy"
    const val TIME_FORMAT_MESSAGE = "hh:mm a"
}