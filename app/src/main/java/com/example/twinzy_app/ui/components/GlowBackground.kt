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
import com.example.twinzy_app.ui.theme.NeonCyan
import com.example.twinzy_app.ui.theme.HotPink
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ParticleBackground(
    modifier: Modifier = Modifier
) {
    val particles = remember { 
        List(50) { 
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                speedX = (Random.nextFloat() - 0.5f) * 0.002f,
                speedY = (Random.nextFloat() - 0.5f) * 0.002f,
                size = Random.nextFloat() * 3f + 1f,
                alpha = Random.nextFloat() * 0.5f + 0.3f
            )
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val newX = (particle.x + particle.speedX * time * 1000) % 1f
            val newY = (particle.y + particle.speedY * time * 1000) % 1f
            
            drawCircle(
                color = if (Random.nextBoolean()) NeonCyan.copy(alpha = particle.alpha) 
                       else HotPink.copy(alpha = particle.alpha),
                radius = particle.size,
                center = Offset(newX * size.width, newY * size.height)
            )
        }
    }
}

@Composable
fun GlowingOrb(
    modifier: Modifier = Modifier,
    color: Color = NeonCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = (size.minDimension / 4f) * scale
        
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.6f),
                    color.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val speedX: Float,
    val speedY: Float,
    val size: Float,
    val alpha: Float
)

