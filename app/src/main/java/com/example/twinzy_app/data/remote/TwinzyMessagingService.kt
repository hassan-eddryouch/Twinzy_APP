package com.example.twinzy_app.data.remote

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class TwinzyMessagingService : FirebaseMessagingService() {
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle FCM messages here
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to server
    }
}