package com.kidslab.physicsquest.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Paleta pensada para niños de 8-12 años: colores vivos, alto contraste, amigables.
val SpaceBlueDark = Color(0xFF1B1B3A)
val SpaceBluePrimary = Color(0xFF3A6FF7)
val SpaceBlueLight = Color(0xFF6FA0FF)
val SkyLight = Color(0xFFEAF2FF)
val SunshineYellow = Color(0xFFFFC93C)
val SunshineYellowLight = Color(0xFFFFE18C)
val CoralAccent = Color(0xFFFF6F59)
val CoralAccentLight = Color(0xFFFFA36C)
val LeafGreen = Color(0xFF35C97B)
val LeafGreenLight = Color(0xFF7FE0A9)
val VioletAccent = Color(0xFF8C6BFA)
val VioletAccentLight = Color(0xFFB79BFF)
val SurfaceWhite = Color(0xFFFFFFFF)
val TextDark = Color(0xFF1E1E2E)
val TextMuted = Color(0xFF5B5B76)
val ErrorSoft = Color(0xFFE7594B)

// Colores extra para fondos decorativos, tarjetas con degradé y estados.
val NightIndigo = Color(0xFF241B4E)
val NightPurple = Color(0xFF3B2A6B)
val CloudWhite = Color(0xFFF6F8FF)
val CardSurfaceAlt = Color(0xFFF0F4FF)
val SuccessBg = Color(0xFFDFF6E4)
val SuccessText = Color(0xFF1F8A4C)
val WarningBg = Color(0xFFFFF1E6)
val WarningText = Color(0xFFB5591E)
val LockedGray = Color(0xFFB8BCD6)

/** Fondo "de noche estelar" para pantallas de portada / introducción (Perfil, Jefe). */
val NightSkyGradient = Brush.verticalGradient(listOf(SpaceBlueDark, NightIndigo, NightPurple))

/** Fondo claro y aireado para las pantallas de navegación diaria (mapa, niveles, inventario). */
val DaySkyGradient = Brush.verticalGradient(listOf(SkyLight, CloudWhite))

/** Un par de colores (oscuro → claro) por cada uno de los cinco mundos, para reconocerlos de un vistazo. */
val WorldGradients: List<Pair<Color, Color>> = listOf(
    SpaceBluePrimary to SpaceBlueLight,
    CoralAccent to CoralAccentLight,
    LeafGreen to LeafGreenLight,
    VioletAccent to VioletAccentLight,
    SunshineYellow to SunshineYellowLight
)

fun worldBrush(order: Int): Brush {
    val (start, end) = WorldGradients[(order - 1).mod(WorldGradients.size)]
    return Brush.linearGradient(listOf(start, end))
}

fun worldColor(order: Int): Color = WorldGradients[(order - 1).mod(WorldGradients.size)].first
