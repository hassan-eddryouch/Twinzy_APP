package com.example.twinzy_app.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.twinzy_app.ui.components.glassmorphism
import com.example.twinzy_app.ui.components.neonGlow
import com.example.twinzy_app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    onMatchClick: (matchId: String, otherUserId: String) -> Unit = { _, _ -> },
    viewModel: MatchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Matches",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = NeonCyan
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is MatchesUiState.Loading -> {
                    LoadingState()
                }
                is MatchesUiState.Success -> {
                    MatchesList(
                        matches = state.matches,
                        isRefreshing = isRefreshing,
                        onRefresh = { viewModel.refresh() },
                        onMatchClick = { matchId, otherUserId ->
                            onMatchClick(matchId, otherUserId)
                        },
                        onUnmatch = { matchId ->
                            viewModel.unmatch(matchId)
                        }
                    )
                }
                is MatchesUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.loadMatches() }
                    )
                }
                is MatchesUiState.Empty -> {
                    EmptyState()
                }
            }
        }
    }
}

@Composable
private fun MatchesList(
    matches: List<MatchWithUser>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onMatchClick: (matchId: String, otherUserId: String) -> Unit,
    onUnmatch: (matchId: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimensions.paddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
    ) {
        items(
            items = matches,
            key = { it.match.matchId }
        ) { matchWithUser ->
            MatchCard(
                matchWithUser = matchWithUser,
                onClick = {
                    onMatchClick(matchWithUser.match.matchId, matchWithUser.otherUser.uid)
                },
                onUnmatch = {
                    onUnmatch(matchWithUser.match.matchId)
                }
            )
        }
    }
}

@Composable
private fun MatchCard(
    matchWithUser: MatchWithUser,
    onClick: () -> Unit,
    onUnmatch: () -> Unit
) {
    var showUnmatchDialog by remember { mutableStateOf(false) }
    val user = matchWithUser.otherUser
    
    if (showUnmatchDialog) {
        androidx.compose.material3.AlertDialog(
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
                    Text("Unmatch", color = ErrorRed)
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
                .padding(Dimensions.paddingMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(64.dp + 6.dp)
                        .then(
                            if (user.isOnline) Modifier.neonGlow(NeonCyan, 32.dp, 8.dp)
                            else Modifier
                        )
                        .border(
                            width = 2.dp,
                            brush = if (user.isOnline) 
                                Brush.sweepGradient(listOf(NeonCyan, NeonMagenta, NeonCyan))
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
                            .background(DarkSurfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                }

                if (user.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .background(NeonCyan, CircleShape)
                            .border(2.dp, DarkBackground, CircleShape)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacingExtraSmall)
            ) {
                Text(
                    text = "${user.name}, ${user.age}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

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
                    Text(
                        text = "Say hi to ${user.name}! 👋",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
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
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
        ) {
            CircularProgressIndicator(
                color = NeonCyan,
                strokeWidth = 3.dp
            )
            Text(
                text = "Loading matches...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium),
            modifier = Modifier.padding(Dimensions.paddingLarge)
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = TextTertiary
            )
            Text(
                text = "No matches yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Start swiping to find your perfect match!",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium),
            modifier = Modifier.padding(Dimensions.paddingLarge)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = ErrorRed
            )
            Text(
                text = "Oops!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(Dimensions.cornerRadiusMedium)
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