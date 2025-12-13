package com.example.twinzy_app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.twinzy_app.ui.theme.*

fun Modifier.glassmorphism(
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    alpha: Float = 0.1f
) = this.then(
    background(
        color = Color.White.copy(alpha = alpha),
        shape = RoundedCornerShape(cornerRadius)
    ).border(
        width = borderWidth,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.3f),
                Color.Transparent
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    ).blur(0.5.dp)
)

fun Modifier.neonGlow(
    color: Color = NeonCyan,
    borderRadius: Dp = 16.dp,
    glowRadius: Dp = 20.dp
) = this.composed {
    val infiniteTransition = rememberInfiniteTransition(label = "neonGlow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neonAlpha"
    )
    
    this.drawBehind {
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = alpha),
                    Color.Transparent
                ),
                radius = glowRadius.toPx()
            ),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(borderRadius.toPx())
        )
    }.border(
        width = 2.dp,
        color = color.copy(alpha = alpha),
        shape = RoundedCornerShape(borderRadius)
    )
}

fun Modifier.cyberBorder(
    color: Color = NeonCyan,
    cornerRadius: Dp = 8.dp,
    strokeWidth: Dp = 2.dp
) = this.composed {
    val infiniteTransition = rememberInfiniteTransition(label = "cyberBorder")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "borderOffset"
    )
    
    this.drawBehind {
        val strokeWidthPx = strokeWidth.toPx()
        val cornerRadiusPx = cornerRadius.toPx()
        
        // Animated dashed border
        val dashLength = 20f
        val gapLength = 10f
        val totalLength = dashLength + gapLength
        
        val path = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = strokeWidthPx / 2,
                    top = strokeWidthPx / 2,
                    right = size.width - strokeWidthPx / 2,
                    bottom = size.height - strokeWidthPx / 2,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx)
                )
            )
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidthPx,
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(dashLength, gapLength),
                    offset
                )
            )
        )
    }
}

fun Modifier.shimmerEffect(
    colors: List<Color> = listOf(
        Color.Transparent,
        Color.White.copy(alpha = 0.3f),
        Color.Transparent
    )
) = this.composed {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    
    this.background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(translateAnim - 100f, translateAnim - 100f),
            end = Offset(translateAnim + 100f, translateAnim + 100f)
        )
    )
}

fun Modifier.pulseEffect(
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    duration: Int = 1000
) = this.composed {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    this.scale(scale)
}

fun Modifier.breathingEffect(
    minAlpha: Float = 0.5f,
    maxAlpha: Float = 1f,
    duration: Int = 2000
) = this.composed {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingAlpha"
    )
    
    this.graphicsLayer { this.alpha = alpha }
}

fun Modifier.scanlineEffect(
    color: Color = NeonCyan,
    lineHeight: Dp = 2.dp,
    speed: Int = 2000
) = this.composed {
    val infiniteTransition = rememberInfiniteTransition(label = "scanline")
    val position by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(speed, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanlinePosition"
    )
    
    this.drawBehind {
        val lineY = size.height * position
        drawLine(
            color = color,
            start = Offset(0f, lineY),
            end = Offset(size.width, lineY),
            strokeWidth = lineHeight.toPx()
        )
    }
}

fun Modifier.hologramEffect() = this.composed {
    val infiniteTransition = rememberInfiniteTransition(label = "hologram")
    
    val glitchOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3000
                0f at 0
                2f at 100
                0f at 200
                -1f at 1500
                0f at 1600
                3f at 2800
                0f at 3000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "hologramGlitch"
    )
    
    val scanlineAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hologramScanline"
    )
    
    this.graphicsLayer {
        translationX = glitchOffset
    }.drawBehind {
        // Horizontal scanlines
        for (y in 0..size.height.toInt() step 4) {
            drawLine(
                color = NeonCyan.copy(alpha = scanlineAlpha * 0.1f),
                start = Offset(0f, y.toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = 1f
            )
        }
    }
}

fun Modifier.matrixRain(
    color: Color = NeonCyan,
    density: Float = 0.1f
) = this.composed {
    val infiniteTransition = rememberInfiniteTransition(label = "matrix")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "matrixTime"
    )
    
    this.drawBehind {
        val columns = (size.width / 20).toInt()
        val rows = (size.height / 20).toInt()
        
        for (col in 0 until columns) {
            for (row in 0 until rows) {
                if (kotlin.random.Random.nextFloat() < density) {
                    val x = col * 20f
                    val y = (row * 20f + time * 2) % size.height
                    val alpha = 1f - (y / size.height)
                    
                    drawCircle(
                        color = color.copy(alpha = alpha * 0.5f),
                        radius = 2f,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}