package com.example.twinzy_app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.twinzy_app.data.model.UserProfile
import com.example.twinzy_app.ui.theme.*

@Composable
fun MatchDialog(
    userProfile: UserProfile,
    onSendMessage: () -> Unit,
    onKeepSwiping: () -> Unit,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(300)) +
                    scaleIn(
                        initialScale = 0.8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
            exit = fadeOut(animationSpec = tween(200)) +
                    scaleOut(targetScale = 0.8f, animationSpec = tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OverlayDark)
                    .padding(Dimensions.paddingLarge),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(Dimensions.cornerRadiusLarge),
                    color = MaterialTheme.customColors.glassWhite,
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.customColors.glassBorder,
                                Color.Transparent
                            )
                        )
                    ),
                    shadowElevation = Dimensions.elevationLarge
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.paddingLarge),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingLarge)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .shadow(
                                    elevation = 20.dp,
                                    shape = CircleShape,
                                    spotColor = NeonMagenta,
                                    ambientColor = NeonMagenta
                                )
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(NeonMagenta, NeonCyan)
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Text(
                            text = "It's a Match!",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "You and ${userProfile.user.name} have liked each other!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .border(2.dp, NeonCyan, CircleShape)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceVariant)
                            ) {
                                AsyncImage(
                                    model = userProfile.user.photos.firstOrNull(),
                                    contentDescription = "Your photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = AccentYellow,
                                modifier = Modifier
                                    .size(40.dp)
                                    .offset(y = (-10).dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .border(2.dp, NeonMagenta, CircleShape)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceVariant)
                            ) {
                                AsyncImage(
                                    model = userProfile.user.photos.firstOrNull(),
                                    contentDescription = "${userProfile.user.name}'s photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
                        ) {
                            Button(
                                onClick = {
                                    visible = false
                                    onSendMessage()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dimensions.buttonHeight),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonCyan
                                ),
                                shape = RoundedCornerShape(Dimensions.cornerRadiusMedium)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Send Message",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    visible = false
                                    onKeepSwiping()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dimensions.buttonHeight),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = TextPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.customColors.glassBorder
                                ),
                                shape = RoundedCornerShape(Dimensions.cornerRadiusMedium)
                            ) {
                                Text(
                                    text = "Keep Swiping",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}