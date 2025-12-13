package com.example.twinzy_app.ui.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.twinzy_app.ui.components.ParticleBackground
import com.example.twinzy_app.ui.main.DiscoverScreen
import com.example.twinzy_app.ui.profile.UserProfileScreen
import com.example.twinzy_app.ui.profile.EditProfileScreen
import com.example.twinzy_app.ui.settings.SettingsScreen
import com.example.twinzy_app.ui.settings.BlockedUsersScreen
import com.example.twinzy_app.ui.chat.ChatListScreen
import com.example.twinzy_app.ui.theme.*

// Using the existing Screen definitions from navigation package

@Composable
fun MainScreen(
    onSignOut: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToChat: (String, String) -> Unit = { _, _ -> }
) {
    HomeScreen(
        onSignOut = onSignOut,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToEditProfile = onNavigateToEditProfile,
        onNavigateToChat = onNavigateToChat
    )
}

@Composable
fun HomeScreen(
    onSignOut: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToChat: (String, String) -> Unit = { _, _ -> }
) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }
    
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            Scaffold(
                containerColor = DeepVoid,
                bottomBar = {
                    AnimatedBottomNavigation(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    when (selectedTab) {
                        0 -> DiscoverScreen(
                            onNavigateToChat = { matchId, userId ->
                                onNavigateToChat(matchId, userId)
                            }
                        )
                        1 -> MatchesScreen(onNavigateToChat = onNavigateToChat)
                        2 -> ChatScreen()
                        3 -> ProfileScreen(
                            onNavigateToSettings = onNavigateToSettings,
                            onNavigateToEditProfile = onNavigateToEditProfile
                        )
                    }
                }
            }
        }
        
        composable("settings") {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEditProfile = {
                    navController.navigate("edit_profile")
                },
                onNavigateToBlockedUsers = {
                    navController.navigate("blocked_users")
                },
                onSignOut = onSignOut
            )
        }
        
        composable("edit_profile") {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("blocked_users") {
            BlockedUsersScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun AnimatedBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = GlassSurface.copy(alpha = 0.95f),
        contentColor = TextPrimary,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem("Discover", Icons.Default.Explore, 0),
            BottomNavItem("Matches", Icons.Default.Favorite, 1),
            BottomNavItem("Chat", Icons.Default.Chat, 2),
            BottomNavItem("Profile", Icons.Default.Person, 3)
        )
        
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedTab == item.index,
                onClick = { onTabSelected(item.index) },
                icon = {
                    AnimatedIcon(
                        icon = item.icon,
                        isSelected = selectedTab == item.index
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NeonCyan,
                    selectedTextColor = NeonCyan,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = NeonCyan.copy(alpha = 0.2f)
                )
            )
        }
    }
}

@Composable
fun AnimatedIcon(
    icon: ImageVector,
    isSelected: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.scale(scale)
    )
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val index: Int
)

@Composable
fun MatchesScreen(onNavigateToChat: (String, String) -> Unit = { _, _ -> }) {
    com.example.twinzy_app.ui.main.MatchesScreen(
        onMatchClick = { matchId, userId ->
            onNavigateToChat(matchId, userId)
        }
    )
}

@Composable
fun ChatScreen() {
    ChatListScreen()
}

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToEditProfile: () -> Unit
) {
    UserProfileScreen(
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToEditProfile = onNavigateToEditProfile
    )
}