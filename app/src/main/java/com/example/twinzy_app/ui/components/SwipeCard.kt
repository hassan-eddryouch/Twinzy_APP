package com.example.twinzy_app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.twinzy_app.data.model.UserProfile
import com.example.twinzy_app.ui.theme.*
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun SwipeCard(
    userProfile: UserProfile,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var currentPhotoIndex by remember { mutableIntStateOf(0) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isDragging) offsetX else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "offsetX"
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = if (isDragging) offsetY else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "offsetY"
    )

    val rotation = (animatedOffsetX / 20f).coerceIn(-15f, 15f)
    val scale = if (isDragging) 0.95f else 1f

    val glowColor = when {
        animatedOffsetX > 50 -> NeonCyan.copy(alpha = (animatedOffsetX / 500f).coerceIn(0f, 0.8f))
        animatedOffsetX < -50 -> DislikeRed.copy(alpha = (abs(animatedOffsetX) / 500f).coerceIn(0f, 0.8f))
        else -> NeonCyan.copy(alpha = 0.1f)
    }

    Box(
        modifier = modifier
            .offset { IntOffset(animatedOffsetX.roundToInt(), animatedOffsetY.roundToInt()) }
            .graphicsLayer {
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        when {
                            offsetX > 300 -> onSwipeRight()
                            offsetX < -300 -> onSwipeLeft()
                        }
                        if (abs(offsetX) <= 300) {
                            offsetX = 0f
                            offsetY = 0f
                        }
                    },
                    onDragCancel = {
                        isDragging = false
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.65f)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(Dimensions.cornerRadiusLarge),
                    spotColor = glowColor,
                    ambientColor = glowColor
                ),
            shape = RoundedCornerShape(Dimensions.cornerRadiusLarge),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationLarge)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = userProfile.user.photos.getOrNull(currentPhotoIndex),
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(Dimensions.cornerRadiusLarge)),
                    contentScale = ContentScale.Crop,
                    alignment = androidx.compose.ui.Alignment.Center
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.9f)
                                ),
                                startY = 300f
                            )
                        )
                )

                if (userProfile.user.photos.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.paddingSmall)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        userProfile.user.photos.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (index == currentPhotoIndex) NeonCyan
                                        else Color.White.copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }
                }

                if (abs(offsetX) > 50) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Dimensions.paddingLarge)
                    ) {
                        if (offsetX > 0) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .graphicsLayer {
                                        alpha = (offsetX / 300f).coerceIn(0f, 1f)
                                        rotationZ = -15f
                                    },
                                shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(4.dp, NeonCyan)
                            ) {
                                Text(
                                    text = "LIKE",
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeonCyan
                                )
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .graphicsLayer {
                                        alpha = (abs(offsetX) / 300f).coerceIn(0f, 1f)
                                        rotationZ = 15f
                                    },
                                shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(4.dp, DislikeRed)
                            ) {
                                Text(
                                    text = "NOPE",
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DislikeRed
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(Dimensions.paddingLarge),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${userProfile.user.name}, ${userProfile.user.age}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (userProfile.user.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(NeonCyan, CircleShape)
                            )
                        }
                    }

                    if (userProfile.distance != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = NeonMagenta,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${userProfile.distance.toInt()} km away",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Interests Chips
                    if (userProfile.user.interests.isNotEmpty()) {
                        androidx.compose.foundation.layout.FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            userProfile.user.interests.take(4).forEach { interest ->
                                DiscoverInterestChip(text = interest)
                            }
                            if (userProfile.user.interests.size > 4) {
                                Text(
                                    text = "+${userProfile.user.interests.size - 4}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (userProfile.user.bio.isNotEmpty()) {
                        Text(
                            text = userProfile.user.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscoverInterestChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.6f),
                        Color.Black.copy(alpha = 0.4f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = NeonCyan.copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = NeonCyan,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium
        )
    }
}