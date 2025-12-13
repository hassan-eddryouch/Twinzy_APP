package com.example.twinzy_app.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.twinzy_app.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HapticClickable(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hapticType: HapticType = HapticType.LIGHT,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(
                bounded = false,
                color = NeonCyan.copy(alpha = 0.3f)
            )
        ) {
            performHapticFeedback(context, hapticType)
            onClick()
        }
    ) {
        content()
    }
}

enum class HapticType {
    LIGHT, MEDIUM, HEAVY, SUCCESS, ERROR
}

private fun performHapticFeedback(context: Context, type: HapticType) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = when (type) {
            HapticType.LIGHT -> VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            HapticType.MEDIUM -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            HapticType.HEAVY -> VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            HapticType.SUCCESS -> VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 100), -1)
            HapticType.ERROR -> VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100, 50, 100), -1)
        }
        vibrator.vibrate(effect)
    } else {
        @Suppress("DEPRECATION")
        when (type) {
            HapticType.LIGHT -> vibrator.vibrate(50)
            HapticType.MEDIUM -> vibrator.vibrate(100)
            HapticType.HEAVY -> vibrator.vibrate(200)
            HapticType.SUCCESS -> vibrator.vibrate(longArrayOf(0, 50, 50, 100), -1)
            HapticType.ERROR -> vibrator.vibrate(longArrayOf(0, 100, 50, 100, 50, 100), -1)
        }
    }
}

@Composable
fun RippleEffect(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    color: Color = NeonCyan
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleScale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleAlpha"
    )
    
    if (isActive) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = (size.minDimension / 4f) * scale
            
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = radius,
                center = center
            )
        }
    }
}

@Composable
fun SuccessAnimation(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {}
) {
    var animationStarted by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible && animationStarted) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { onAnimationEnd() },
        label = "successScale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isVisible && animationStarted) 360f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "successRotation"
    )
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            animationStarted = true
        }
    }
    
    if (isVisible) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            // Background glow
            Box(
                modifier = Modifier
                    .size((100 * scale).dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Success.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            // Success particles
            Canvas(
                modifier = Modifier.size(80.dp)
            ) {
                val particleCount = 12
                val radius = size.minDimension / 3f
                
                for (i in 0 until particleCount) {
                    val angle = (i * 360f / particleCount) + rotation
                    val x = center.x + cos(Math.toRadians(angle.toDouble())).toFloat() * radius * scale
                    val y = center.y + sin(Math.toRadians(angle.toDouble())).toFloat() * radius * scale
                    
                    drawCircle(
                        color = Success.copy(alpha = 0.8f),
                        radius = 3f * scale,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorShake(
    isShaking: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shake")
    
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isShaking) 10f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shakeOffset"
    )
    
    Box(
        modifier = modifier.offset(x = offsetX.dp)
    ) {
        content()
    }
}

@Composable
fun PressEffect(
    isPressed: Boolean,
    modifier: Modifier = Modifier,
    scaleDown: Float = 0.95f,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "pressScale"
    )
    
    Box(
        modifier = modifier.graphicsLayer {
            this.scaleX = scale
            this.scaleY = scale
        }
    ) {
        content()
    }
}

@Composable
fun GlowEffect(
    isGlowing: Boolean,
    modifier: Modifier = Modifier,
    color: Color = NeonCyan,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    
    Box(modifier = modifier) {
        if (isGlowing) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                color.copy(alpha = glowAlpha * 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        content()
    }
}