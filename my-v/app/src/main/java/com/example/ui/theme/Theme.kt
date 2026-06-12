package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    primaryContainer = LightElectricBlue,
    onPrimary = PureWhite,
    onPrimaryContainer = PureWhite,
    secondary = SuccessGreen,
    secondaryContainer = SuccessGreenContainer,
    onSecondary = OnSuccessGreen,
    tertiary = WarmAccent,
    tertiaryContainer = WarmAccentContainer,
    background = CoolBg,
    onBackground = OnSurfaceText,
    surface = PureWhite,
    onSurface = OnSurfaceText,
    surfaceVariant = SurfaceContainer,
    onSurfaceVariant = OnSurfaceVariantText,
    outline = OutlineBorder,
    outlineVariant = OutlineVariant,
    error = ErrorRed,
    errorContainer = ErrorRedContainer,
    onErrorContainer = OnErrorRedContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = LightElectricBlue,
    primaryContainer = ElectricBlue,
    onPrimary = PureWhite,
    background = OnSurfaceText,
    onBackground = CoolBg,
    surface = OnSurfaceVariantText,
    onSurface = CoolBg,
    outline = OutlineVariant,
    outlineVariant = OutlineBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
