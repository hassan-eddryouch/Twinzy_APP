package com.example.twinzy_app.ui.swipe

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.twinzy_app.R
import com.example.twinzy_app.data.model.User
import com.example.twinzy_app.ui.components.ParticleBackground
import com.example.twinzy_app.ui.theme.*
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeScreen(
    onNavigateToChat: (String) -> Unit = {}
) {
    val mockUsers = remember {
        listOf(
            User(uid = "1", name = "Alex", age = 25, bio = "Love cyberpunk aesthetics", photos = listOf(""), interests = listOf("Gaming", "Art")),
            User(uid = "2", name = "Sam", age = 28, bio = "Future tech enthusiast", photos = listOf(""), interests = listOf("Tech", "Music")),
            User(uid = "3", name = "Jordan", age = 24, bio = "Digital artist", photos = listOf(""), interests = listOf("Art", "Movies"))
        )
    }
    
    var profiles by remember { mutableStateOf(mockUsers) }
    var showMatchDialog by remember { mutableStateOf(false) }
    var matchedUser by remember { mutableStateOf<User?>(null) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepVoid, GlassSurface.copy(alpha = 0.1f))
                )
            )
    ) {
        ParticleBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = stringResource(R.string.discover),
                style = MaterialTheme.typography.headlineLarge,
                color = NeonCyan
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (profiles.isNotEmpty()) {
                    profiles.take(3).reversed().forEachIndexed { index, user ->
                        SwipeCard(
                            user = user,
                            isTopCard = index == profiles.size - 1,
                            onSwipeLeft = {
                                profiles = profiles.filter { it.uid != user.uid }
                            },
                            onSwipeRight = {
                                profiles = profiles.filter { it.uid != user.uid }
                                matchedUser = user
                                showMatchDialog = true
                            },
                            modifier = Modifier
                                .offset(y = (index * 8).dp)
                                .scale(1f - (index * 0.05f))
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.no_more_profiles),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ActionButtons(
                onDislike = {
                    profiles.firstOrNull()?.let { user ->
                        profiles = profiles.filter { it.uid != user.uid }
                    }
                },
                onSuperLike = {
                    profiles.firstOrNull()?.let { user ->
                        profiles = profiles.filter { it.uid != user.uid }
                        matchedUser = user
                        showMatchDialog = true
                    }
                },
                onLike = {
                    profiles.firstOrNull()?.let { user ->
                        profiles = profiles.filter { it.uid != user.uid }
                        matchedUser = user
                        showMatchDialog = true
                    }
                }
            )
        }
    }
    
    if (showMatchDialog && matchedUser != null) {
        com.example.twinzy_app.ui.components.MatchDialog(
            userProfile = com.example.twinzy_app.data.model.UserProfile(
                user = matchedUser!!,
                distance = 5.0,
                commonInterests = emptyList()
            ),
            onSendMessage = {
                showMatchDialog = false
                onNavigateToChat(matchedUser!!.uid)
            },
            onKeepSwiping = {
                showMatchDialog = false
            },
            onDismiss = {
                showMatchDialog = false
            }
        )
    }
}

@Composable
fun SwipeCard(
    user: User,
    isTopCard: Boolean,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var rotation by remember { mutableStateOf(0f) }
    
    Card(
        modifier = modifier
            .width(340.dp)
            .height(520.dp)
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .rotate(rotation)
            .graphicsLayer {
                alpha = 1f - (abs(offsetX) / 1000f)
            }
            .pointerInput(Unit) {
                if (isTopCard) {
                    detectDragGestures(
                        onDragEnd = {
                            when {
                                offsetX > 300 -> onSwipeRight()
                                offsetX < -300 -> onSwipeLeft()
                                else -> {
                                    offsetX = 0f
                                    offsetY = 0f
                                    rotation = 0f
                                }
                            }
                        },
                        onDrag = { _, dragAmount ->
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                            rotation = (offsetX / 20f).coerceIn(-15f, 15f)
                        }
                    )
                }
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(GlassSurface, GlassSurfaceVariant)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = TextTertiary
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                OverlayDark
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Text(
                    text = "${user.name}, ${user.age}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = user.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    user.interests.take(3).forEach { interest ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GlassSurface.copy(alpha = 0.8f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = interest,
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButtons(
    onDislike: () -> Unit,
    onSuperLike: () -> Unit,
    onLike: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(
            icon = Icons.Default.Close,
            color = Error,
            onClick = onDislike
        )
        
        ActionButton(
            icon = Icons.Default.Star,
            color = Warning,
            onClick = onSuperLike
        )
        
        ActionButton(
            icon = Icons.Default.Favorite,
            color = Success,
            onClick = onLike
        )
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    IconButton(
        onClick = {
            isPressed = true
            onClick()
            isPressed = false
        },
        modifier = Modifier
            .size(64.dp)
            .scale(scale)
            .shadow(12.dp, CircleShape, spotColor = color)
            .background(
                brush = Brush.radialGradient(
                    listOf(color.copy(alpha = 0.8f), color.copy(alpha = 0.6f))
                ),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(32.dp)
        )
    }
}

