package com.example.twinzy_app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.twinzy_app.ui.theme.NeonCyan
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CyberScanner(
    modifier: Modifier = Modifier,
    isScanning: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    
    val scanAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanAngle"
    )
    
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    val gridOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridOffset"
    )
    
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f
            
            if (isScanning) {
                // Draw grid background
                val gridSize = 20f
                for (x in 0..((size.width / gridSize).toInt() + 1)) {
                    val xPos = x * gridSize + (gridOffset % gridSize)
                    drawLine(
                        color = NeonCyan.copy(alpha = 0.1f),
                        start = Offset(xPos, 0f),
                        end = Offset(xPos, size.height),
                        strokeWidth = 1f
                    )
                }
                
                for (y in 0..((size.height / gridSize).toInt() + 1)) {
                    val yPos = y * gridSize + (gridOffset % gridSize)
                    drawLine(
                        color = NeonCyan.copy(alpha = 0.1f),
                        start = Offset(0f, yPos),
                        end = Offset(size.width, yPos),
                        strokeWidth = 1f
                    )
                }
                
                // Draw scanning circles
                for (i in 1..4) {
                    val circleRadius = radius * (i / 4f)
                    drawCircle(
                        color = NeonCyan.copy(alpha = pulseAlpha * (0.6f / i)),
                        radius = circleRadius,
                        center = center,
                        style = Stroke(width = 2f)
                    )
                }
                
                // Draw scanning arc
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            NeonCyan.copy(alpha = pulseAlpha * 0.3f),
                            NeonCyan.copy(alpha = pulseAlpha),
                            Color.Transparent
                        )
                    ),
                    startAngle = scanAngle - 30f,
                    sweepAngle = 60f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                )
                
                // Draw corner brackets
                val bracketSize = 20f
                val corners = listOf(
                    Offset(center.x - radius + bracketSize, center.y - radius),
                    Offset(center.x + radius - bracketSize, center.y - radius),
                    Offset(center.x - radius + bracketSize, center.y + radius),
                    Offset(center.x + radius - bracketSize, center.y + radius)
                )
                
                corners.forEach { corner ->
                    drawLine(
                        color = NeonCyan.copy(alpha = pulseAlpha),
                        start = corner,
                        end = Offset(corner.x + bracketSize, corner.y),
                        strokeWidth = 3f
                    )
                }
            }
            
            // Draw center dot
            drawCircle(
                color = if (isScanning) NeonCyan.copy(alpha = pulseAlpha) else NeonCyan,
                radius = 6f,
                center = center
            )
        }
    }
}

@Composable
fun UploadProgressScanner(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "uploadProgress"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 3f
        
        // Background circle
        drawCircle(
            color = NeonCyan.copy(alpha = 0.2f),
            radius = radius,
            center = center,
            style = Stroke(width = 8f)
        )
        
        // Progress arc
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(NeonCyan, com.example.twinzy_app.ui.theme.HotPink)
            ),
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            style = Stroke(width = 8f, cap = androidx.compose.ui.graphics.StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
    }
}