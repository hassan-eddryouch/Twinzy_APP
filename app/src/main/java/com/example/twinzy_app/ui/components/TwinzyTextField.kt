package com.example.twinzy_app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.twinzy_app.ui.theme.*

@Composable
fun TwinzyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused) NeonCyan else OverlayGlass,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "border"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 0.6f else 0f,
        animationSpec = tween(300),
        label = "glow"
    )

    Box(modifier = modifier) {
        if (glowAlpha > 0) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(16.dp)
                    .background(
                        NeonCyan.copy(alpha = glowAlpha),
                        RoundedCornerShape(16.dp)
                    )
            )
        }
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.textFieldHeight)
                .border(
                    width = 2.dp,
                    color = animatedBorderColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(
                    color = GlassSurface.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(16.dp)
                ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledTextColor = TextTertiary,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedLabelColor = NeonCyan,
                unfocusedLabelColor = TextSecondary,
                cursorColor = NeonCyan
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun TwinzyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    icon: @Composable (() -> Unit)? = null
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val backgroundColor = if (isPrimary) {
        Brush.horizontalGradient(listOf(NeonCyan, HotPink))
    } else {
        Brush.horizontalGradient(listOf(GlassSurface, GlassSurfaceVariant))
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.buttonHeight)
            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (isPrimary) NeonCyan else Color.Transparent
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = GlassSurface.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor, RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                icon?.invoke()
                if (icon != null) Spacer(Modifier.width(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isPrimary) DeepVoid else TextPrimary
                )
            }
        }
    }
}
