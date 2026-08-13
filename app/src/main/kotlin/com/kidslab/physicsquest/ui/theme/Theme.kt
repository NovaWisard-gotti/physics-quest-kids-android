package com.kidslab.physicsquest.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PhysicsQuestColorScheme = lightColorScheme(
    primary = SpaceBluePrimary,
    onPrimary = SurfaceWhite,
    secondary = SunshineYellow,
    onSecondary = TextDark,
    tertiary = CoralAccent,
    background = SkyLight,
    onBackground = TextDark,
    surface = SurfaceWhite,
    onSurface = TextDark,
    error = ErrorSoft,
    onError = SurfaceWhite
)

@Composable
fun PhysicsQuestTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PhysicsQuestColorScheme,
        typography = PhysicsQuestTypography,
        content = content
    )
}
