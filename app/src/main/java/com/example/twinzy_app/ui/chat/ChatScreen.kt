package com.example.twinzy_app.ui.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.twinzy_app.ui.chat.components.ChatBubble
import com.example.twinzy_app.ui.components.ParticleBackground
import com.example.twinzy_app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToProfile: (String) -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val messageText by viewModel.messageText.collectAsState()
    val isSending by viewModel.isSending.collectAsState()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            when (val state = uiState) {
                is ChatUiState.Success -> {
                    ChatTopBar(
                        user = state.otherUser,
                        onNavigateBack = onNavigateBack,
                        onNavigateToProfile = { onNavigateToProfile(state.otherUser.uid) }
                    )
                }
                else -> {
                    TopAppBar(
                        title = { Text("Chat", color = NeonCyan) },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = NeonCyan
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = NeonCyan
                        )
                    )
                }
            }
        },
        // FIX: Replaced 'DeepDarkVoid' with 'DarkBackground' from your Color.kt
        containerColor = DarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ParticleBackground()

            when (val state = uiState) {
                is ChatUiState.Loading -> {
                    LoadingState()
                }
                is ChatUiState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Messages list
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            state = listState,
                            contentPadding = PaddingValues(vertical = 16.dp),
                            reverseLayout = true
                        ) {
                            items(
                                items = state.messages.reversed(),
                                key = { it.messageId }
                            ) { message ->
                                val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                                val isCurrentUser = message.senderId == currentUserId

                                // Staggered entrance animation
                                var isVisible by remember { mutableStateOf(false) }
                                LaunchedEffect(Unit) {
                                    isVisible = true
                                }

                                AnimatedVisibility(
                                    visible = isVisible,
                                    enter = slideInHorizontally(
                                        initialOffsetX = { if (isCurrentUser) it else -it },
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    ) + fadeIn()
                                ) {
                                    ChatBubble(
                                        message = message,
                                        isCurrentUser = isCurrentUser,
                                        showTimestamp = true
                                    )
                                }
                            }
                        }

                        // Message input
                        MessageInput(
                            messageText = messageText,
                            onMessageTextChange = { viewModel.updateMessageText(it) },
                            onSendMessage = {
                                viewModel.sendMessage()
                                scope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            },
                            onAttachImage = { /* Handle image attachment */ },
                            isSending = isSending
                        )
                    }

                    // Auto-scroll to bottom when new message arrives
                    LaunchedEffect(state.messages.size) {
                        if (state.messages.isNotEmpty()) {
                            listState.animateScrollToItem(0)
                        }
                    }
                }
                is ChatUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.refresh() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopBar(
    user: com.example.twinzy_app.data.model.User,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToProfile),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingLarge)
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .border(
                                width = 1.dp,
                                brush = if (user.isOnline)
                                    Brush.sweepGradient(listOf(NeonCyan, NeonMagenta))
                                else
                                    androidx.compose.ui.graphics.SolidColor(Color.Transparent),
                                shape = CircleShape
                            )
                            .padding(2.dp)
                    ) {
                        AsyncImage(
                            model = user.photos.firstOrNull(),
                            contentDescription = "Profile photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(DarkSurfaceVariant),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (user.isOnline) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.BottomEnd)
                                .background(NeonCyan, CircleShape)
                                .border(2.dp, DarkBackground, CircleShape)
                                // Fixed Shadow with named arguments
                                .shadow(
                                    elevation = 4.dp,
                                    shape = CircleShape,
                                    spotColor = NeonCyan,
                                    ambientColor = NeonCyan
                                )
                        )
                    }
                }

                Column {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (user.isOnline) "Online" else "Offline",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (user.isOnline) NeonCyan else TextSecondary
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonCyan
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Video call */ }) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call",
                    tint = MaterialTheme.customColors.glassBorder
                )
            }
            IconButton(onClick = { /* More options */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.customColors.glassBorder
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = NeonCyan
        )
    )
}

@Composable
private fun MessageInput(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onAttachImage: () -> Unit,
    isSending: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.customColors.glassWhite,
        shadowElevation = Dimensions.elevationMedium,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.customColors.glassBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.paddingMedium),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
        ) {
            // Attach button
            IconButton(
                onClick = onAttachImage,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Attach",
                    tint = NeonCyan
                )
            }

            // Text field
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Type a message...",
                        color = TextTertiary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = MaterialTheme.customColors.glassBorder,
                    cursorColor = NeonCyan,
                    focusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(Dimensions.cornerRadiusLarge),
                maxLines = 4
            )

            // Send button
            val canSend = messageText.isNotBlank() && !isSending

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (canSend)
                            Brush.linearGradient(listOf(NeonCyan, NeonMagenta))
                        else
                            androidx.compose.ui.graphics.SolidColor(DarkSurfaceVariant)
                    )
                    .clickable(enabled = canSend, onClick = onSendMessage),
                contentAlignment = Alignment.Center
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (canSend) Color.White else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingLarge)
        ) {
            CircularProgressIndicator(
                color = NeonCyan,
                strokeWidth = 3.dp
            )
            Text(
                text = "Loading chat...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
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
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = ErrorRed
            )
            Text(
                text = "Oops!",
                style = AppTypography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = message,
                style = AppTypography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Again", fontWeight = FontWeight.Bold)
            }
        }
    }
}