package com.example.twinzy_app.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.twinzy_app.ui.onboarding.OnboardingScreen
import com.example.twinzy_app.ui.splash.SplashScreen
import com.example.twinzy_app.ui.auth.*
import com.example.twinzy_app.ui.main.MainScreen
import com.example.twinzy_app.ui.profile.*
import com.example.twinzy_app.ui.chat.*
import com.example.twinzy_app.ui.settings.SettingsScreen

@Composable
fun TwinzyNavGraph(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsState()
    
    // No automatic navigation - let splash screen handle initial routing
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.CompleteProfile.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToAuth = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Welcome.route) {
            AuthScreen(
                onNavigateToProfile = {
                    navController.navigate(Screen.CompleteProfile.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToPhone = {
                    navController.navigate(Screen.PhoneAuth.route)
                }
            )
        }
        
        composable(Screen.PhoneAuth.route) {
            PhoneAuthScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.CompleteProfile.route) {
                        popUpTo(Screen.PhoneAuth.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.PhoneAuth.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.CompleteProfile.route) {
            ProfileCreationScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Main.route) {
            MainScreen(
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToChat = { matchId, otherUserId ->
                    navController.navigate(Screen.ChatDetail.createRoute(matchId, otherUserId))
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.ChatDetail.route,
            arguments = listOf(
                navArgument("matchId") { type = NavType.StringType },
                navArgument("otherUserId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: ""
            ChatDetailScreen(
                matchId = matchId,
                otherUserId = otherUserId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProfile = { userId ->
                    // Navigate to profile screen if implemented
                }
            )
        }
        
        composable(Screen.SignIn.route) {
            SignInScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.CompleteProfile.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
