package com.example.twinzy_app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.example.twinzy_app.ui.theme.*
import kotlin.math.*
import kotlinx.coroutines.delay

@Composable
fun LoadingAnimation(
    modifier: Modifier = Modifier,
    color: Color = NeonCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Canvas(modifier = modifier.size(40.dp)) {
        rotate(rotation) {
            drawArc(
                color = color,
                startAngle = 0f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

@Composable
fun CyberLoadingSpinner(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cyberRotation"
    )
    
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cyberPulse"
    )
    
    Box(
        modifier = modifier.size(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 3f
            
            rotate(rotation, center) {
                for (i in 0..7) {
                    val angle = i * 45f
                    val startRadius = radius * 0.7f
                    val endRadius = radius * pulse
                    
                    val startX = center.x + cos(Math.toRadians(angle.toDouble())).toFloat() * startRadius
                    val startY = center.y + sin(Math.toRadians(angle.toDouble())).toFloat() * startRadius
                    val endX = center.x + cos(Math.toRadians(angle.toDouble())).toFloat() * endRadius
                    val endY = center.y + sin(Math.toRadians(angle.toDouble())).toFloat() * endRadius
                    
                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(NeonCyan, HotPink),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY)
                        ),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
        
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonCyan, Color.Transparent)
                    )
                )
        )
    }
}

@Composable
fun PulsingLoader(
    modifier: Modifier = Modifier,
    dotCount: Int = 3,
    color: Color = NeonCyan
) {
    val dots = remember { List(dotCount) { Animatable(0f) } }
    
    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay(index * 150L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 with LinearOutSlowInEasing
                        1.0f at 300 with LinearOutSlowInEasing
                        0.0f at 600 with LinearOutSlowInEasing
                        0.0f at 1200
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dots.forEach { animatable ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                color.copy(alpha = animatable.value),
                                color.copy(alpha = animatable.value * 0.3f)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun WaveLoader(
    modifier: Modifier = Modifier,
    color: Color = NeonCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )
    
    Canvas(modifier = modifier.size(width = 80.dp, height = 40.dp)) {
        val waveHeight = size.height * 0.3f
        val waveLength = size.width / 3f
        
        for (x in 0..size.width.toInt() step 2) {
            val normalizedX = x / size.width
            val y = size.height / 2f + sin(normalizedX * 2 * PI + phase) * waveHeight
            
            drawCircle(
                color = color.copy(alpha = 0.7f),
                radius = 2f,
                center = Offset(x.toFloat(), y.toFloat())
            )
        }
    }
}

