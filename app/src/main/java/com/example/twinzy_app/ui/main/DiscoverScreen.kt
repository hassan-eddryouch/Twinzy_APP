package com.example.twinzy_app.ui.main

import androidx.compose.animation.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.twinzy_app.data.model.SwipeAction
import com.example.twinzy_app.data.model.UserProfile
import com.example.twinzy_app.ui.components.*
import com.example.twinzy_app.ui.theme.*
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    onNavigateToChat: (String, String) -> Unit = { _, _ -> },
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var showMatchDialog by remember { mutableStateOf(false) }
    var matchedUser by remember { mutableStateOf<UserProfile?>(null) }

    LaunchedEffect(uiState.swipeResult) {
        uiState.swipeResult?.let { result ->
            if (result.isMatch && result.userProfile != null) {
                matchedUser = result.userProfile
                showMatchDialog = true
            }
            viewModel.clearSwipeResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CyberLogo()
                        Text(
                            text = "Discover",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = NeonCyan
                        )
                    }
                }
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ParticleBackground()

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CyberLoadingSpinner()
                    }
                }
                uiState.currentUserProfile != null -> {
                    DiscoverContent(
                        userProfile = uiState.currentUserProfile!!,
                        onSwipe = { action ->
                            when (action) {
                                SwipeAction.LIKE -> viewModel.swipeRight(uiState.currentUserProfile!!.user.uid)
                                SwipeAction.DISLIKE -> viewModel.swipeLeft(uiState.currentUserProfile!!.user.uid)
                                SwipeAction.SUPER_LIKE -> viewModel.superLike(uiState.currentUserProfile!!.user.uid)
                            }
                        }
                    )
                }
                uiState.error != null -> {
                    ErrorState(
                        message = uiState.error!!,
                        onRetry = { viewModel.refresh() }
                    )
                }
                else -> {
                    EmptyState(
                        onRefresh = { viewModel.refresh() }
                    )
                }
            }
        }
    }

    if (showMatchDialog && matchedUser != null) {
        MatchDialog(
            userProfile = matchedUser!!,
            onSendMessage = {
                showMatchDialog = false
                onNavigateToChat("match_${matchedUser!!.user.uid}", matchedUser!!.user.uid)
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
private fun DiscoverContent(
    userProfile: UserProfile,
    onSwipe: (SwipeAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            SwipeCard(
                userProfile = userProfile,
                onSwipeLeft = { onSwipe(SwipeAction.DISLIKE) },
                onSwipeRight = { onSwipe(SwipeAction.LIKE) },
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        ActionButtons(
            onDislike = { onSwipe(SwipeAction.DISLIKE) },
            onSuperLike = { onSwipe(SwipeAction.SUPER_LIKE) },
            onLike = { onSwipe(SwipeAction.LIKE) }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}



@Composable
private fun SwipeIndicators(offsetX: Float, offsetY: Float) {
    // Like Indicator
    AnimatedVisibility(
        visible = offsetX > 50,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .padding(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(NeonCyan.copy(alpha = 0.9f))
                .padding(16.dp)
        ) {
            Text(
                text = "LIKE",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    // Dislike Indicator
    AnimatedVisibility(
        visible = offsetX < -50,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .padding(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ErrorRed.copy(alpha = 0.9f))
                .padding(16.dp)
        ) {
            Text(
                text = "NOPE",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    // Super Like Indicator
    AnimatedVisibility(
        visible = offsetY < -100,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .padding(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(NeonMagenta.copy(alpha = 0.9f))
                .padding(16.dp)
        ) {
            Text(
                text = "SUPER LIKE",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onDislike: () -> Unit,
    onSuperLike: () -> Unit,
    onLike: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            onClick = onDislike,
            icon = Icons.Default.Close,
            color = ErrorRed,
            size = 64f
        )

        ActionButton(
            onClick = onSuperLike,
            icon = Icons.Default.Star,
            color = NeonMagenta,
            size = 56f
        )

        Box {
            ActionButton(
                onClick = onLike,
                icon = Icons.Default.Favorite,
                color = NeonCyan,
                size = 72f
            )
            PulsingHeart(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.Center)
                    .offset(y = (-2).dp),
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}





@Composable
private fun CyberLogo() {
    Box(
        modifier = Modifier
            .size(32.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = NeonCyan
            )
            .background(
                Brush.linearGradient(listOf(NeonCyan, NeonMagenta)),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}



@Composable
private fun EmptyState(onRefresh: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = NeonCyan.copy(alpha = 0.7f)
            )

            Text(
                text = "No more profiles",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Check back later for new people in your area, or adjust your preferences.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRefresh,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Refresh",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = ErrorRed
            )

            Text(
                text = "Oops!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Try Again",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}