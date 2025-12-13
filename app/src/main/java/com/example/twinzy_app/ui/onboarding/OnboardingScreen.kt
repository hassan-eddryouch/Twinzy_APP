package com.example.twinzy_app.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.twinzy_app.R
import com.example.twinzy_app.ui.components.*
import com.example.twinzy_app.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: Int,
    val description: Int,
    val color: androidx.compose.ui.graphics.Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToAuth: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        OnboardingPage(R.string.onboarding_title_1, R.string.onboarding_desc_1, NeonCyan),
        OnboardingPage(R.string.onboarding_title_2, R.string.onboarding_desc_2, HotPink),
        OnboardingPage(R.string.onboarding_title_3, R.string.onboarding_desc_3, Success)
    )
    
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepVoid, GlassSurface.copy(alpha = 0.3f))
                )
            )
    ) {
        ParticleBackground()
        CyberGrid(modifier = Modifier.fillMaxSize())
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(pages[page])
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val animatedSize by animateFloatAsState(
                        targetValue = if (isSelected) 12f else 8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "indicatorSize"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(animatedSize.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) {
                                    Brush.radialGradient(
                                        listOf(NeonCyan, HotPink)
                                    )
                                } else {
                                    Brush.radialGradient(
                                        listOf(TextTertiary, androidx.compose.ui.graphics.Color.Transparent)
                                    )
                                }
                            )
                    )
                }
            }
            
            NeonButton(
                text = if (pagerState.currentPage == pages.size - 1) {
                    stringResource(R.string.get_started)
                } else {
                    stringResource(R.string.continue_btn)
                },
                onClick = {
                    if (pagerState.currentPage == pages.size - 1) {
                        viewModel.completeOnboarding()
                        onNavigateToAuth()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    var visible by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    GlowingOrb(
                        modifier = Modifier.size(200.dp),
                        color = page.color
                    )
                    
                    when (page.color) {
                        NeonCyan -> {
                            PulsingHeart(
                                modifier = Modifier.size(80.dp),
                                color = HotPink
                            )
                        }
                        HotPink -> {
                            CyberScanner(
                                modifier = Modifier.size(120.dp),
                                isScanning = true
                            )
                        }
                        else -> {
                            FloatingElements(
                                modifier = Modifier.size(150.dp),
                                count = 10
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(page.description),
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}