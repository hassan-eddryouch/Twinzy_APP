package com.example.twinzy_app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.twinzy_app.ui.theme.*

enum class TwinzyButtonStyle {
    PRIMARY, // Neon Cyan
    SECONDARY, // Neon Magenta
    OUTLINED,
    TEXT,
    GRADIENT, // Cyan to Magenta
    GLASS
}

@Composable
fun TwinzyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: TwinzyButtonStyle = TwinzyButtonStyle.PRIMARY,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    fullWidth: Boolean = false
) {
    val buttonShape = RoundedCornerShape(Dimensions.cornerRadiusMedium)

    when (style) {
        TwinzyButtonStyle.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = if (fullWidth) modifier.fillMaxWidth() else modifier,
                enabled = enabled && !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color.Black,
                    disabledContainerColor = NeonCyan.copy(alpha = 0.3f),
                    disabledContentColor = Color.Black.copy(alpha = 0.5f)
                ),
                shape = buttonShape,
                contentPadding = PaddingValues(
                    horizontal = Dimensions.paddingLarge,
                    vertical = Dimensions.paddingMedium
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 2.dp
                )
            ) {
                ButtonContent(text, loading, icon)
            }
        }
        TwinzyButtonStyle.SECONDARY -> {
            Button(
                onClick = onClick,
                modifier = if (fullWidth) modifier.fillMaxWidth() else modifier,
                enabled = enabled && !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonMagenta,
                    contentColor = Color.White,
                    disabledContainerColor = NeonMagenta.copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = buttonShape,
                contentPadding = PaddingValues(
                    horizontal = Dimensions.paddingLarge,
                    vertical = Dimensions.paddingMedium
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 2.dp
                )
            ) {
                ButtonContent(text, loading, icon)
            }
        }
        TwinzyButtonStyle.OUTLINED -> {
            OutlinedButton(
                onClick = onClick,
                modifier = if (fullWidth) modifier.fillMaxWidth() else modifier,
                enabled = enabled && !loading,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NeonCyan,
                    disabledContentColor = NeonCyan.copy(alpha = 0.3f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (enabled) NeonCyan else NeonCyan.copy(alpha = 0.3f)
                ),
                shape = buttonShape,
                contentPadding = PaddingValues(
                    horizontal = Dimensions.paddingLarge,
                    vertical = Dimensions.paddingMedium
                )
            ) {
                ButtonContent(text, loading, icon)
            }
        }
        TwinzyButtonStyle.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled && !loading,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = NeonCyan,
                    disabledContentColor = NeonCyan.copy(alpha = 0.5f)
                ),
                contentPadding = PaddingValues(
                    horizontal = Dimensions.paddingMedium,
                    vertical = Dimensions.paddingSmall
                )
            ) {
                ButtonContent(text, loading, icon)
            }
        }
        TwinzyButtonStyle.GRADIENT -> {
            Button(
                onClick = onClick,
                modifier = if (fullWidth) modifier.fillMaxWidth() else modifier,
                enabled = enabled && !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Black.copy(alpha = 0.5f)
                ),
                shape = buttonShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (enabled) {
                                Modifier.background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(NeonCyan, NeonMagenta)
                                    ),
                                    shape = buttonShape
                                )
                            } else {
                                Modifier.background(
                                    color = DarkSurfaceVariant,
                                    shape = buttonShape
                                )
                            }
                        )
                        .padding(
                            horizontal = Dimensions.paddingLarge,
                            vertical = Dimensions.paddingMedium
                        )
                ) {
                    ButtonContent(text, loading, icon)
                }
            }
        }
        TwinzyButtonStyle.GLASS -> {
             Button(
                onClick = onClick,
                modifier = if (fullWidth) modifier.fillMaxWidth() else modifier,
                enabled = enabled && !loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.customColors.glassWhite,
                    contentColor = NeonCyan,
                    disabledContainerColor = MaterialTheme.customColors.glassWhite.copy(alpha = 0.1f),
                    disabledContentColor = NeonCyan.copy(alpha = 0.3f)
                ),
                shape = buttonShape,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.customColors.glassBorder
                ),
                contentPadding = PaddingValues(
                    horizontal = Dimensions.paddingLarge,
                    vertical = Dimensions.paddingMedium
                )
            ) {
                ButtonContent(text, loading, icon)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    loading: Boolean,
    icon: ImageVector?
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(24.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        if (loading) {
            LoadingDots(color = LocalContentColor.current)
        } else {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    size: Float = 60f
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "actionScale"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 0.4f,
        animationSpec = tween(200),
        label = "actionGlow"
    )
    
    Box(
        modifier = modifier
            .size(size.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(8.dp)
                .background(
                    color.copy(alpha = glowAlpha),
                    CircleShape
                )
        )
        
        Surface(
            onClick = {
                isPressed = true
                onClick()
                isPressed = false
            },
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
            color = color,
            shadowElevation = 8.dp
        ) {
            Box(
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size((size * 0.4f).dp)
                )
            }
        }
    }
}