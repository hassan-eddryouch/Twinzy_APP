package com.example.twinzy_app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.twinzy_app.ui.theme.*

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = if (enabled) 12.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (enabled) NeonCyan else Color.Transparent
            ),
        enabled = enabled && !loading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Black,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Black.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (enabled) {
                        Brush.horizontalGradient(
                            colors = listOf(NeonCyan, HotPink)
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                GlassSurface.copy(alpha = 0.3f),
                                GlassSurface.copy(alpha = 0.3f)
                            )
                        )
                    },
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            if (loading) {
                LoadingDots(color = Color.Black)
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color.Black else TextSecondary
                )
            }
        }
    }
}