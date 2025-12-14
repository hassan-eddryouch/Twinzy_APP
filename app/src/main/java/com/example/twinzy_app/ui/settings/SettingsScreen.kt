package com.example.twinzy_app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.twinzy_app.ui.components.glassmorphism
import com.example.twinzy_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToBlockedUsers: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onNavigateToAuth: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    // Handle navigation events
    LaunchedEffect(Unit) {
        viewModel.signOutEvent.collect {
            onSignOut()
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.navigateToAuthEvent.collect {
            onNavigateToAuth()
        }
    }
    var showNotifications by remember { mutableStateOf(true) }
    var showOnlineStatus by remember { mutableStateOf(true) }
    var showDistance by remember { mutableStateOf(true) }
    var maxDistance by remember { mutableFloatStateOf(50f) }
    var ageRangeStart by remember { mutableFloatStateOf(18f) }
    var ageRangeEnd by remember { mutableFloatStateOf(35f) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    
    val languages = listOf(
        "en" to "English",
        "fr" to "Français", 
        "ar" to "العربية"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
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
                    containerColor = Color.Transparent,
                    titleContentColor = NeonCyan
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Section
            item {
                SectionHeader("Account")
            }

            item {
                SettingsCard {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Person,
                            title = "Edit Profile",
                            subtitle = "Change your photos and details",
                            onClick = onNavigateToEditProfile
                        )
                        
                        HorizontalDivider(color = MaterialTheme.customColors.glassBorder, modifier = Modifier.padding(horizontal = 16.dp))
                        
                        SettingsItem(
                            icon = Icons.Default.Language,
                            title = "Language",
                            subtitle = languages.find { it.first == uiState.selectedLanguage }?.second ?: "English",
                            onClick = { showLanguageDialog = true }
                        )
                    }
                }
            }

            // Discovery Preferences
            item {
                SectionHeader("Discovery Preferences")
            }

            item {
                SettingsCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Max Distance
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Maximum Distance",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${maxDistance.toInt()} km",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = maxDistance,
                                onValueChange = { maxDistance = it },
                                valueRange = 1f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonCyan,
                                    activeTrackColor = NeonCyan,
                                    inactiveTrackColor = MaterialTheme.customColors.glassBorder
                                )
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.customColors.glassBorder)

                        // Age Range
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Age Range",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${ageRangeStart.toInt()} - ${ageRangeEnd.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            RangeSlider(
                                value = ageRangeStart..ageRangeEnd,
                                onValueChange = { range ->
                                    ageRangeStart = range.start
                                    ageRangeEnd = range.endInclusive
                                },
                                valueRange = 18f..80f,
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonCyan,
                                    activeTrackColor = NeonCyan,
                                    inactiveTrackColor = MaterialTheme.customColors.glassBorder
                                )
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.customColors.glassBorder)

                        // Show Distance
                        SettingsSwitchItem(
                            title = "Show Distance",
                            subtitle = "Display distance on profiles",
                            checked = showDistance,
                            onCheckedChange = { showDistance = it }
                        )
                    }
                }
            }

            // Privacy & Safety
            item {
                SectionHeader("Privacy & Safety")
            }

            item {
                SettingsCard {
                    Column {
                        SettingsSwitchItem(
                            title = "Show Online Status",
                            subtitle = "Let others see when you're online",
                            checked = showOnlineStatus,
                            onCheckedChange = { showOnlineStatus = it }
                        )

                        HorizontalDivider(color = MaterialTheme.customColors.glassBorder, modifier = Modifier.padding(horizontal = 16.dp))

                        SettingsItem(
                            icon = Icons.Default.Block,
                            title = "Blocked Users",
                            subtitle = "Manage blocked accounts",
                            onClick = onNavigateToBlockedUsers
                        )
                        
                        HorizontalDivider(color = MaterialTheme.customColors.glassBorder, modifier = Modifier.padding(horizontal = 16.dp))
                        
                        SettingsItem(
                            icon = Icons.Default.DeleteForever,
                            title = "Delete Account",
                            subtitle = "Permanently delete your account",
                            onClick = { showDeleteAccountDialog = true }
                        )
                    }
                }
            }

            // Notifications
            item {
                SectionHeader("Notifications")
            }

            item {
                SettingsCard {
                    SettingsSwitchItem(
                        title = "Push Notifications",
                        subtitle = "Get notified about matches and messages",
                        checked = showNotifications,
                        onCheckedChange = { showNotifications = it }
                    )
                }
            }

            // Sign Out
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Button(
                    onClick = { showSignOutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.customColors.glassBorder
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                            tint = ErrorRed
                        )
                        Text(
                            text = "Sign Out",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }
                }
            }
        }
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = {
                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        viewModel.onSignOutClicked()
                    }
                ) {
                    Text(
                        text = "Sign Out",
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSignOutDialog = false }
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.customColors.primary
                    )
                }
            },
            containerColor = GlassSurface
        )
    }
    
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = "Select Language",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    languages.forEach { (code, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(code)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.selectedLanguage == code,
                                onClick = {
                                    viewModel.setLanguage(code)
                                    showLanguageDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = NeonCyan,
                                    unselectedColor = MaterialTheme.customColors.glassBorder
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showLanguageDialog = false }
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.customColors.primary
                    )
                }
            },
            containerColor = GlassSurface
        )
    }
    
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text(
                    text = "Delete Account",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ErrorRed
                )
            },
            text = {
                Text(
                    text = "This action cannot be undone. All your data, matches, and messages will be permanently deleted.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        viewModel.onDeleteAccountConfirmed()
                    },
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ErrorRed,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Delete",
                            color = ErrorRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAccountDialog = false },
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.customColors.primary
                    )
                }
            },
            containerColor = GlassSurface
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = NeonCyan,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(cornerRadius = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        content()
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.customColors.glassBorder
        )
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonCyan,
                checkedTrackColor = NeonCyan.copy(alpha = 0.3f),
                uncheckedThumbColor = MaterialTheme.customColors.glassBorder,
                uncheckedTrackColor = GlassSurface
            )
        )
    }
}