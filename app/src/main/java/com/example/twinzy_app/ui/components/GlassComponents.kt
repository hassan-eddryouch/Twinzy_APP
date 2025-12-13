package com.example.twinzy_app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.twinzy_app.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = GlassSurface.copy(alpha = 0.4f),
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = OverlayGlass,
                shape = RoundedCornerShape(24.dp)
            )
            .blur(0.5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = GlassSurface.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            content()
        }
    }
}



@Composable
fun GlassBottomSheet(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        GlassSurface.copy(alpha = 0.9f)
                    )
                )
            ),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = GlassSurface.copy(alpha = 0.8f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            OverlayGlass
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        OverlayGlass,
                        RoundedCornerShape(2.dp)
                    )
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun NeonDivider(
    modifier: Modifier = Modifier,
    color: Color = NeonCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "divider")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dividerAlpha"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        color.copy(alpha = alpha),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
fun CyberChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) NeonCyan else OverlayGlass,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chipColor"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chipScale"
    )
    
    Surface(
        onClick = onClick,
        modifier = modifier.scale(scale),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) animatedColor.copy(alpha = 0.2f) else GlassSurface.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = animatedColor
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) animatedColor else TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}