package com.example.twinzy_app.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.twinzy_app.ui.components.ParticleBackground
import com.example.twinzy_app.ui.theme.*

@Composable
fun ChatListScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
    ) {
        ParticleBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = "Messages",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matches yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }
    }
}