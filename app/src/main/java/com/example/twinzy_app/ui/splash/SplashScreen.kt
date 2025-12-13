package com.example.twinzy_app.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.twinzy_app.R
import com.example.twinzy_app.ui.components.*
import com.example.twinzy_app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    authViewModel: com.example.twinzy_app.ui.auth.AuthViewModel,
    viewModel: SplashViewModel = hiltViewModel()
) {
    var startAnimation by remember { mutableStateOf(false) }
    var clickCount by remember { mutableStateOf(0) }
    
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (startAnimation) 360f else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "rotation"
    )
    
    val uiState by viewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
    }
    
    LaunchedEffect(authState, uiState) {
        if (!uiState.isLoading && startAnimation) {
            // Navigation logic based on authentication and profile completion
            when {
                // User is authenticated and has complete profile -> Main app
                authState.isAuthenticated && !authState.needsProfileCompletion -> {
                    onNavigateToHome()
                }
                // User is authenticated but needs to complete profile
                authState.isAuthenticated && authState.needsProfileCompletion -> {
                    onNavigateToProfile()
                }
                // User not authenticated but onboarding completed -> Auth
                !authState.isAuthenticated && uiState.isOnboardingCompleted -> {
                    onNavigateToAuth()
                }
                // First time user -> Onboarding
                !uiState.isOnboardingCompleted -> {
                    onNavigateToOnboarding()
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepVoid, DeepVoid.copy(alpha = 0.8f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        ParticleBackground()
        CyberGrid(modifier = Modifier.fillMaxSize())
        FloatingElements(modifier = Modifier.fillMaxSize())
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                GlowingOrb(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale)
                        .rotate(rotation),
                    color = NeonCyan
                )
                
                PulsingHeart(
                    modifier = Modifier
                        .size(60.dp)
                        .scale(scale),
                    color = HotPink
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = NeonCyan,
                modifier = Modifier
                    .scale(scale)
                    .clickable {
                        clickCount++
                        if (clickCount >= 3) {
                            viewModel.enableDevMode()
                            clickCount = 0
                        }
                    }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (startAnimation) {
                LoadingDots(
                    modifier = Modifier.scale(scale),
                    color = HotPink
                )
            }
        }
    }
}