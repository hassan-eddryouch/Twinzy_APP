package com.example.twinzy_app.ui.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.twinzy_app.data.model.MessageType
import com.example.twinzy_app.ui.components.ParticleBackground
import com.example.twinzy_app.ui.theme.*
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    matchId: String,
    otherUserId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val messageText by viewModel.messageText.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    LaunchedEffect(matchId, otherUserId) {
        viewModel.initialize(matchId, otherUserId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val state = uiState) {
                        is ChatUiState.Success -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    onNavigateToProfile(state.otherUser.uid)
                                }
                            ) {
                                AsyncImage(
                                    model = state.otherUser.photos.firstOrNull(),
                                    contentDescription = "Profile photo",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(GlassSurface),
                                    contentScale = ContentScale.Crop
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column {
                                    Text(
                                        text = "${state.otherUser.name}, ${state.otherUser.age}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (state.otherUser.isOnline) "Online" else "Offline",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (state.otherUser.isOnline) Success else TextSecondary
                                    )
                                }
                            }
                        }
                        else -> {
                            Text(
                                text = "Loading...",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepVoid
                )
            )
        },
        bottomBar = {
            Surface(
                color = GlassSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = viewModel::updateMessageText,
                        placeholder = { Text("Type a message...", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = GlassSurface,
                            cursorColor = NeonCyan
                        ),
                        shape = RoundedCornerShape(24.dp),
                        enabled = !isSending
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = viewModel::sendMessage,
                        enabled = messageText.isNotBlank() && !isSending,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (messageText.isNotBlank() && !isSending) NeonCyan else GlassSurface,
                                CircleShape
                            )
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = TextSecondary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (messageText.isNotBlank()) DeepVoid else TextSecondary
                            )
                        }
                    }
                }
            }
        },
        containerColor = DeepVoid
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ParticleBackground()
            
            when (val state = uiState) {
                is ChatUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NeonCyan)
                    }
                }
                is ChatUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                is ChatUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (state.messages.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = GlassSurface.copy(alpha = 0.7f)
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text(
                                            text = "You matched! Start the conversation.",
                                            modifier = Modifier.padding(12.dp),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                        
                        items(state.messages) { message ->
                            MessageBubble(
                                message = message,
                                isFromCurrentUser = message.senderId == currentUserId,
                                onEdit = { messageId, newContent -> viewModel.onMessageEdit(messageId, newContent) },
                                onDelete = { messageId -> viewModel.onMessageDelete(messageId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessageBubble(
    message: com.example.twinzy_app.data.model.Message,
    isFromCurrentUser: Boolean,
    onEdit: (String, String) -> Unit,
    onDelete: (String) -> Unit
) {
    var showContextMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf("") }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isFromCurrentUser) NeonCyan else GlassSurface
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isFromCurrentUser) 16.dp else 4.dp,
                bottomEnd = if (isFromCurrentUser) 4.dp else 16.dp
            ),
            modifier = Modifier
                .widthIn(max = 280.dp)
                .then(
                    if (isFromCurrentUser) {
                        Modifier.combinedClickable(
                            onLongClick = { showContextMenu = true },
                            onClick = {}
                        )
                    } else Modifier
                )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                if (message.messageType == MessageType.TEXT) {
                    Text(
                        text = message.text,
                        color = if (isFromCurrentUser) DeepVoid else TextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else if (message.messageType == MessageType.IMAGE && message.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = message.imageUrl,
                        contentDescription = "Image message",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                        .format(java.util.Date(message.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isFromCurrentUser) DeepVoid.copy(alpha = 0.7f) else TextSecondary,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
    
    if (showContextMenu) {
        AlertDialog(
            onDismissRequest = { showContextMenu = false },
            title = { Text("Message Options", color = TextPrimary) },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            editText = message.text
                            showContextMenu = false
                            showEditDialog = true
                        }
                    ) {
                        Text("Edit", color = NeonCyan)
                    }
                    TextButton(
                        onClick = {
                            onDelete(message.messageId)
                            showContextMenu = false
                        }
                    ) {
                        Text("Delete", color = ErrorRed)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showContextMenu = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = GlassSurface
        )
    }
    
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Message", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = editText,
                    onValueChange = { editText = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassSurface,
                        cursorColor = NeonCyan
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editText.isNotBlank()) {
                            onEdit(message.messageId, editText)
                        }
                        showEditDialog = false
                    }
                ) {
                    Text("Save", color = NeonCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = GlassSurface
        )
    }
}