package com.jasiri.fieldofficer.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Copper,
    onPrimary = Color.White,
    primaryContainer = CopperLight,
    onPrimaryContainer = CopperDark,
    secondary = Navy,
    onSecondary = Color.White,
    background = Cream,
    onBackground = Charcoal,
    surface = Color.White,
    onSurface = Charcoal,
    surfaceVariant = Sand,
    onSurfaceVariant = Muted,
    error = Danger,
    onError = Color.White,
    errorContainer = DangerLight,
    onErrorContainer = Danger,
)

private val DarkColors = darkColorScheme(
    primary = Copper,
    onPrimary = Color.White,
    primaryContainer = CopperDark,
    onPrimaryContainer = CopperLight,
    secondary = Sand,
    onSecondary = NavyDark,
    background = NavyDark,
    onBackground = Cream,
    surface = Charcoal,
    onSurface = Cream,
    surfaceVariant = Charcoal,
    onSurfaceVariant = Muted,
    error = Danger,
    onError = Color.White,
    errorContainer = DangerLight,
    onErrorContainer = Danger,
)

// Default Material 3 type scale for now — swap in brand-specific fonts/sizes
// once the real design system is finalized.
val FieldOfficerTypography = Typography()

@Composable
fun FieldOfficerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = FieldOfficerTypography,
        content = content
    )
}
