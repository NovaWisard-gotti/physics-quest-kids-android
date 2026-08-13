package com.kidslab.physicsquest.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.theme.SunshineYellow
import com.kidslab.physicsquest.ui.theme.TextMuted

/** Fila de 3 estrellas, usada en el mapa, la pantalla de niveles y el resultado de un puzzle. */
@Composable
fun StarsRow(starsEarned: Int, modifier: Modifier = Modifier, starSize: androidx.compose.ui.unit.Dp = 22.dp) {
    Row(modifier = modifier) {
        repeat(3) { index ->
            val filled = index < starsEarned
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = if (filled) SunshineYellow else TextMuted,
                modifier = Modifier.size(starSize)
            )
        }
    }
}

/** Tarjeta de mensaje de resultado (éxito o intento fallido), siempre con tono positivo y alentador. */
@Composable
fun FeedbackBanner(message: String, success: Boolean, modifier: Modifier = Modifier) {
    val backgroundColor = if (success) Color(0xFFDFF6E4) else Color(0xFFFFF1E6)
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

/** Botón de pista: solo aparece habilitado después de 2 intentos fallidos, y nunca resta puntos. */
@Composable
fun HintButton(available: Boolean, hintText: String?, onRequestHint: () -> Unit, modifier: Modifier = Modifier) {
    if (!available) return
    Button(onClick = onRequestHint, modifier = modifier) {
        Text(if (hintText == null) "💡 Pedir una pista" else "💡 $hintText")
    }
}
