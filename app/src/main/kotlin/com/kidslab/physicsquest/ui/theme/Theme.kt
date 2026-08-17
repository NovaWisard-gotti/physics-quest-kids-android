package com.kidslab.physicsquest.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PhysicsQuestColorScheme = lightColorScheme(
    primary = SpaceBluePrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = SkyLight,
    onPrimaryContainer = SpaceBlueDark,
    secondary = SunshineYellow,
    onSecondary = TextDark,
    secondaryContainer = SunshineYellowLight,
    onSecondaryContainer = TextDark,
    tertiary = CoralAccent,
    onTertiary = SurfaceWhite,
    tertiaryContainer = CoralAccentLight,
    onTertiaryContainer = TextDark,
    background = SkyLight,
    onBackground = TextDark,
    surface = SurfaceWhite,
    onSurface = TextDark,
    surfaceVariant = CardSurfaceAlt,
    onSurfaceVariant = TextMuted,
    error = ErrorSoft,
    onError = SurfaceWhite
)

@Composable
fun PhysicsQuestTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PhysicsQuestColorScheme,
        typography = PhysicsQuestTypography,
        shapes = PhysicsQuestShapes,
        content = content
    )
}
