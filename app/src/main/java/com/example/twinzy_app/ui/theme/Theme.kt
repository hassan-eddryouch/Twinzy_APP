package com.example.twinzy_app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = HotPink,
    tertiary = Color(0xFF8B5CF6),
    background = DeepVoid,
    surface = GlassSurface,
    onPrimary = DeepVoid,
    onSecondary = DeepVoid,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

data class CustomColors(
    val glassBorder: Color = NeonCyan.copy(alpha = 0.3f),
    val glassWhite: Color = Color.White.copy(alpha = 0.1f),
    val primary: Color = NeonCyan,
    val secondary: Color = HotPink,
    val background: Color = DeepVoid,
    val surface: Color = GlassSurface
)

val MaterialTheme.customColors: CustomColors
    @Composable
    @ReadOnlyComposable
    get() = CustomColors()

@Composable
fun TwinzyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}