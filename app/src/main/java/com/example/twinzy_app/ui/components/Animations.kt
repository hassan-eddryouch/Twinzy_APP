package com.example.twinzy_app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp
import com.example.twinzy_app.ui.theme.*
import kotlin.math.*
import kotlin.random.Random

@Composable
fun PulsingHeart(
    modifier: Modifier = Modifier,
    color: Color = NeonMagenta
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heart")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartAlpha"
    )
    
    Canvas(modifier = modifier) {
        val heartPath = Path().apply {
            val width = size.width
            val height = size.height
            
            moveTo(width / 2f, height * 0.25f)
            cubicTo(
                width * 0.2f, height * 0.1f,
                -width * 0.25f, height * 0.6f,
                width / 2f, height
            )
            cubicTo(
                width * 1.25f, height * 0.6f,
                width * 0.8f, height * 0.1f,
                width / 2f, height * 0.25f
            )
        }
        
        scale(scale, scale) {
            drawPath(
                path = heartPath,
                color = color.copy(alpha = alpha)
            )
        }
    }
}

@Composable
fun FloatingElements(
    modifier: Modifier = Modifier,
    count: Int = 20
) {
    val elements = remember {
        List(count) {
            FloatingElement(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speed = Random.nextFloat() * 0.5f + 0.2f,
                size = Random.nextFloat() * 8f + 4f,
                color = if (Random.nextBoolean()) NeonCyan else HotPink
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "floatTime"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        elements.forEach { element ->
            val progress = (time * element.speed) % 1f
            val yPos = size.height * (1f + progress)
            val xOffset = sin(progress * 4 * PI) * 50f
            
            drawCircle(
                color = element.color.copy(alpha = 0.6f),
                radius = element.size,
                center = Offset(
                    size.width * element.x + xOffset.toFloat(),
                    yPos - size.height * 0.1f
                )
            )
        }
    }
}

@Composable
fun CyberGrid(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "grid")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gridOffset"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val gridSize = 50f
        val strokeWidth = 1f
        
        for (x in 0..((size.width / gridSize).toInt() + 1)) {
            val xPos = x * gridSize + (offset % gridSize)
            drawLine(
                color = NeonCyan.copy(alpha = 0.1f),
                start = Offset(xPos, 0f),
                end = Offset(xPos, size.height),
                strokeWidth = strokeWidth
            )
        }
        
        for (y in 0..((size.height / gridSize).toInt() + 1)) {
            val yPos = y * gridSize + (offset % gridSize)
            drawLine(
                color = NeonCyan.copy(alpha = 0.1f),
                start = Offset(0f, yPos),
                end = Offset(size.width, yPos),
                strokeWidth = strokeWidth
            )
        }
    }
}

@Composable
fun NeonBorder(
    modifier: Modifier = Modifier,
    color: Color = NeonCyan,
    strokeWidth: Float = 2f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neon")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neonAlpha"
    )
    
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        color.copy(alpha = alpha * 0.1f),
                        Color.Transparent
                    )
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
    )
}

@Composable
fun LoadingDots(
    modifier: Modifier = Modifier,
    color: Color = NeonCyan
) {
    val dots = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )
    
    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay(index * 100L)
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        dots.forEach { animatable ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = animatable.value))
            )
        }
    }
}

private data class FloatingElement(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val color: Color
)