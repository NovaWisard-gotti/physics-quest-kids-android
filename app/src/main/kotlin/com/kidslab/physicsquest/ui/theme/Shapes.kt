package com.kidslab.physicsquest.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Esquinas bien redondeadas en toda la app: se ven más suaves y "juguetonas"
// para el público infantil que las esquinas cuadradas por defecto de Material.
val PhysicsQuestShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

val PillShape = RoundedCornerShape(50)
