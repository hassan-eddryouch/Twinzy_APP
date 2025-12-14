package com.example.twinzy_app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.example.twinzy_app.ui.theme.*
import kotlin.math.*

@Composable
fun CyberScanningEffect(
    modifier: Modifier = Modifier,
    isScanning: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val scanLineAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isScanning) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            ) {
                drawCyberRadar(
                    radarRotation = radarRotation,
                    pulseScale = pulseScale,
                    scanLineAlpha = scanLineAlpha
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "SCANNING",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonCyan
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "UPLOADING...",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

private fun DrawScope.drawCyberRadar(
    radarRotation: Float,
    pulseScale: Float,
    scanLineAlpha: Float
) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = size.minDimension / 2
    
    // Draw concentric circles
    for (i in 1..4) {
        val circleRadius = radius * (i / 4f) * pulseScale
        drawCircle(
            color = NeonCyan,
            radius = circleRadius,
            center = center,
            style = Stroke(
                width = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
            ),
            alpha = 0.3f
        )
    }
    
    // Draw radar sweep
    rotate(radarRotation, center) {
        val sweepGradient = Brush.sweepGradient(
            0f to Color.Transparent,
            0.3f to NeonCyan.copy(alpha = scanLineAlpha * 0.8f),
            0.6f to NeonCyan.copy(alpha = scanLineAlpha),
            1f to Color.Transparent,
            center = center
        )
        
        drawCircle(
            brush = sweepGradient,
            radius = radius,
            center = center
        )
    }
    
    // Draw scanning line
    rotate(radarRotation, center) {
        drawLine(
            color = NeonCyan,
            start = center,
            end = Offset(center.x + radius, center.y),
            strokeWidth = 2.dp.toPx(),
            alpha = scanLineAlpha
        )
    }
    
    // Draw grid lines
    val gridLines = 8
    for (i in 0 until gridLines) {
        val angle = (360f / gridLines) * i
        rotate(angle, center) {
            drawLine(
                color = HotPink,
                start = center,
                end = Offset(center.x + radius, center.y),
                strokeWidth = 0.5.dp.toPx(),
                alpha = 0.2f
            )
        }
    }
    
    // Draw center dot
    drawCircle(
        color = NeonCyan,
        radius = 3.dp.toPx(),
        center = center
    )
}