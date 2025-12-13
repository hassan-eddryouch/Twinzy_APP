package com.example.twinzy_app.ui.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.twinzy_app.R
import com.example.twinzy_app.ui.components.ParticleBackground
import com.example.twinzy_app.ui.components.TwinzyButton
import com.example.twinzy_app.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    
    // Real user data from ViewModel
    val currentUser = uiState.currentUser
    val userName = currentUser?.name ?: "Unknown User"
    val userAge = currentUser?.age ?: 0
    val userBio = currentUser?.bio?.takeIf { it.isNotBlank() } ?: "No bio available"
    val userPhotos = currentUser?.photos ?: emptyList()
    val userInterests = currentUser?.interests ?: emptyList()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
    ) {
        ParticleBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Parallax Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                if (userPhotos.isNotEmpty()) {
                    AsyncImage(
                        model = userPhotos.first(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(y = (scrollState.value * 0.5f).dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
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
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    DeepVoid.copy(alpha = 0f),
                                    DeepVoid
                                )
                            )
                        )
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.paddingLarge)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (userAge > 0) "$userName, $userAge" else userName,
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = userBio,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                    
                    IconButton(
                        onClick = { /* Edit profile */ },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(listOf(NeonCyan, HotPink)),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_profile),
                            tint = DeepVoid
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Interests",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (userInterests.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        userInterests.take(3).forEach { interest ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GlassSurface.copy(alpha = 0.6f))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = interest,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = NeonCyan
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "No interests added yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                TwinzyButton(
                    text = stringResource(R.string.settings),
                    onClick = { /* Navigate to settings */ },
                    isPrimary = false,
                    icon = {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = NeonCyan)
                    }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                TwinzyButton(
                    text = stringResource(R.string.logout),
                    onClick = { /* Logout */ },
                    isPrimary = false,
                    icon = {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = HotPink)
                    }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
