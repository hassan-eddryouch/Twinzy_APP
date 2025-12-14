package com.example.twinzy_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.twinzy_app.ui.main.MatchWithUser
import com.example.twinzy_app.ui.theme.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MatchCard(
    matchWithUser: MatchWithUser,
    onClick: () -> Unit,
    onUnmatch: () -> Unit
) {
    var showUnmatchDialog by remember { mutableStateOf(false) }
    val user = matchWithUser.otherUser
    
    if (showUnmatchDialog) {
        AlertDialog(
            onDismissRequest = { showUnmatchDialog = false },
            title = { Text("Unmatch ${user.name}?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUnmatch()
                        showUnmatchDialog = false
                    }
                ) {
                    Text("Unmatch", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnmatchDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(cornerRadius = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Photo
            Box {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .then(
                            if (user.isOnline) Modifier.neonGlow(NeonCyan, 32.dp, 8.dp)
                            else Modifier
                        )
                        .border(
                            width = 2.dp,
                            brush = if (user.isOnline) 
                                Brush.sweepGradient(listOf(NeonCyan, HotPink, NeonCyan))
                            else 
                                androidx.compose.ui.graphics.SolidColor(Color.Transparent),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                ) {
                    AsyncImage(
                        model = user.photos.firstOrNull(),
                        contentDescription = "Profile photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(GlassSurface),
                        contentScale = ContentScale.Crop,
                        alignment = androidx.compose.ui.Alignment.Center
                    )
                }

                if (user.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .background(NeonCyan, CircleShape)
                            .border(2.dp, DeepVoid, CircleShape)
                    )
                }
            }

            // User Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${user.name}, ${user.age}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Interests Chips
                if (user.interests.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        user.interests.take(3).forEach { interest ->
                            InterestChip(text = interest)
                        }
                        if (user.interests.size > 3) {
                            Text(
                                text = "+${user.interests.size - 3}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    val lastMessageText = when {
                        matchWithUser.lastMessage != null -> {
                            val message = matchWithUser.lastMessage
                            val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                            val prefix = if (message.senderId == currentUserId) "You: " else ""
                            "$prefix${message.text}"
                        }
                        else -> "Say hi to ${user.name}! 👋"
                    }
                    
                    Text(
                        text = lastMessageText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = { showUnmatchDialog = true },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun InterestChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.2f),
                        HotPink.copy(alpha = 0.2f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = NeonCyan.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = NeonCyan,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}