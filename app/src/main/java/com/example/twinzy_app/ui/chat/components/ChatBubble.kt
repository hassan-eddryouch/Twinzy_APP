package com.example.twinzy_app.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor // Import this
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.twinzy_app.data.model.Message
import com.example.twinzy_app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatBubble(
    message: Message,
    isCurrentUser: Boolean,
    showTimestamp: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isCurrentUser) 48.dp else Dimensions.paddingMedium,
                end = if (isCurrentUser) Dimensions.paddingMedium else 48.dp,
                top = Dimensions.spacingExtraSmall,
                bottom = Dimensions.spacingExtraSmall
            ),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        // Message bubble with Neon Gradient / Glassmorphism

        val bubbleShape = RoundedCornerShape(
            topStart = if (isCurrentUser) Dimensions.cornerRadiusMedium else 4.dp,
            topEnd = if (isCurrentUser) 4.dp else Dimensions.cornerRadiusMedium,
            bottomStart = Dimensions.cornerRadiusMedium,
            bottomEnd = Dimensions.cornerRadiusMedium
        )

        Box(
            modifier = Modifier
                .widthIn(max = Dimensions.chatBubbleMaxWidth)
                .shadow(
                    elevation = if (isCurrentUser) 8.dp else 4.dp,
                    shape = bubbleShape,
                    spotColor = if (isCurrentUser) NeonMagenta else NeonCyan,
                    ambientColor = if (isCurrentUser) NeonMagenta else NeonCyan
                )
                .clip(bubbleShape)
                .background(
                    // FIX: Both branches must return a Brush.
                    // We wrap the Color in SolidColor()
                    brush = if (isCurrentUser) {
                        Brush.linearGradient(
                            colors = listOf(NeonMagenta.copy(alpha = 0.8f), NeonCyan.copy(alpha = 0.8f))
                        )
                    } else {
                        SolidColor(MaterialTheme.customColors.glassWhite)
                    }
                )
                .border(
                    width = 1.dp,
                    brush = if (isCurrentUser)
                        Brush.linearGradient(colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent))
                    else
                        Brush.linearGradient(colors = listOf(NeonCyan.copy(alpha = 0.5f), Color.Transparent)),
                    shape = bubbleShape
                )
        ) {
            Box(
                modifier = Modifier.padding(Dimensions.chatBubblePadding)
            ) {
                if (message.imageUrl.isNotEmpty()) {
                    // Image message
                    AsyncImage(
                        model = message.imageUrl,
                        contentDescription = "Sent image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .clip(RoundedCornerShape(Dimensions.cornerRadiusSmall)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Text message
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isCurrentUser) Color.White else TextPrimary
                    )
                }
            }
        }

        // Timestamp
        if (showTimestamp) {
            Text(
                text = formatMessageTime(message.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                modifier = Modifier.padding(
                    top = 4.dp,
                    start = if (isCurrentUser) 0.dp else 8.dp,
                    end = if (isCurrentUser) 8.dp else 0.dp
                )
            )
        }
    }
}

private fun formatMessageTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
        diff < 604800_000 -> {
            val sdf = SimpleDateFormat("EEE HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
        else -> {
            val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}