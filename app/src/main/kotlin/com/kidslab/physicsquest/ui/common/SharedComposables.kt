package com.kidslab.physicsquest.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.theme.PillShape
import com.kidslab.physicsquest.ui.theme.SuccessBg
import com.kidslab.physicsquest.ui.theme.SuccessText
import com.kidslab.physicsquest.ui.theme.SunshineYellow
import com.kidslab.physicsquest.ui.theme.SunshineYellowLight
import com.kidslab.physicsquest.ui.theme.TextDark
import com.kidslab.physicsquest.ui.theme.TextMuted
import com.kidslab.physicsquest.ui.theme.WarningBg
import com.kidslab.physicsquest.ui.theme.WarningText
import kotlinx.coroutines.delay

/**
 * Barra superior reutilizable con flecha de regreso, usada en toda pantalla
 * a la que se llega navegando "hacia adentro" (niveles, jefe, inventario,
 * puzzles). Sin esto, un niño solo podía volver con el gesto/botón físico
 * del sistema, que no siempre es evidente. [onLight] controla el contraste
 * para poder usarla también sobre los fondos oscuros (pantalla del jefe).
 */
@Composable
fun QuestTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    onLight: Boolean = true
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(4.dp, CircleShape, clip = false)
                .clip(CircleShape)
                .background(if (onLight) Color.White else Color.White.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = if (onLight) accentColor else Color.White)
            }
        }
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            color = if (onLight) TextDark else Color.White,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

/** Fila estática de 3 estrellas, usada en listas (mapa, niveles, inventario). */
@Composable
fun StarsRow(starsEarned: Int, modifier: Modifier = Modifier, starSize: Dp = 22.dp) {
    Row(modifier = modifier) {
        repeat(3) { index ->
            val filled = index < starsEarned
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = if (filled) SunshineYellow else TextMuted.copy(alpha = 0.45f),
                modifier = Modifier.size(starSize)
            )
        }
    }
}

/**
 * Fila de estrellas con aparición animada, una por una, para celebrar un
 * puzzle recién superado (más satisfactorio que mostrarlas todas de golpe).
 */
@Composable
fun AnimatedStarsRow(starsEarned: Int, modifier: Modifier = Modifier, starSize: Dp = 30.dp) {
    var visibleCount by remember(starsEarned) { mutableStateOf(0) }
    LaunchedEffect(starsEarned) {
        visibleCount = 0
        repeat(3) { i ->
            delay(150L)
            visibleCount = i + 1
        }
    }
    Row(modifier = modifier) {
        repeat(3) { index ->
            val filled = index < starsEarned
            val shown = index < visibleCount
            val scale by animateFloatAsState(
                targetValue = if (shown) 1f else 0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "starScale"
            )
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = if (filled) SunshineYellow else TextMuted.copy(alpha = 0.45f),
                modifier = Modifier
                    .size(starSize)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
            )
        }
    }
}

/** Tarjeta de mensaje de resultado (éxito o intento fallido), siempre con tono positivo y alentador. */
@Composable
fun FeedbackBanner(message: String, success: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(220)) + scaleIn(
            initialScale = 0.85f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        ),
        modifier = modifier
    ) {
        val backgroundColor = if (success) SuccessBg else WarningBg
        val textColor = if (success) SuccessText else WarningText
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (success) "🎉" else "🤔",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Text(text = message, style = MaterialTheme.typography.bodyLarge, color = textColor)
            }
        }
    }
}

/** Botón grande, redondeado y con animación de "presión", para la acción principal de cada pantalla. */
@Composable
fun QuestPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "questButtonScale")
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.35f),
            disabledContentColor = contentColor.copy(alpha = 0.7f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp, disabledElevation = 0.dp),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Text(
            text = if (icon != null) "$icon  $text" else text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

/** Versión secundaria (contorno) del botón principal, para acciones auxiliares. */
@Composable
fun QuestOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: String? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "questOutlinedScale")
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = PillShape,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
        contentPadding = PaddingValues(vertical = 14.dp, horizontal = 20.dp),
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        Text(
            text = if (icon != null) "$icon  $text" else text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Botón de pista: solo aparece habilitado después de 2 intentos fallidos, y nunca resta puntos. */
@Composable
fun HintButton(available: Boolean, hintText: String?, onRequestHint: () -> Unit, modifier: Modifier = Modifier) {
    if (!available) return
    QuestOutlinedButton(
        text = hintText ?: "Pedir una pista",
        icon = "💡",
        onClick = onRequestHint,
        accentColor = TextDark,
        modifier = modifier
    )
}

/** Tarjetita amigable para mostrar una pista ya revelada. */
@Composable
fun HintNote(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SunshineYellowLight.copy(alpha = 0.4f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Text("💡", modifier = Modifier.padding(end = 8.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, color = TextDark)
        }
    }
}

/** Insignia circular con degradé, usada para números de mundo/nivel, candados o coronas. */
@Composable
fun CircleEmblem(
    brush: Brush,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(6.dp, CircleShape, clip = false)
            .clip(CircleShape)
            .background(brush),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/** Encabezado de sección con una barrita de color, para separar bloques dentro de una pantalla. */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier, accent: Color = MaterialTheme.colorScheme.primary) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(width = 6.dp, height = 22.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(accent)
        )
        Text(text, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 10.dp))
    }
}

/**
 * Barra fina que marca, dentro del rango total de un control (por ejemplo
 * un [Slider]), la franja de valores que cuenta como correcta. Sin esto,
 * un niño no tiene forma de saber si "todo a la izquierda" es la posición
 * correcta o si el objetivo está más hacia el centro.
 */
@Composable
fun RangeHint(
    valueRange: ClosedFloatingPointRange<Float>,
    targetRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val totalSpan = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)
    val startFrac = ((targetRange.start - valueRange.start) / totalSpan).coerceIn(0f, 1f)
    val endFrac = ((targetRange.endInclusive - valueRange.start) / totalSpan).coerceIn(0f, 1f)
    BoxWithConstraints(modifier.fillMaxWidth().height(10.dp)) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(5.dp))
                .background(TextMuted.copy(alpha = 0.15f))
        )
        Box(
            Modifier
                .offset(x = maxWidth * startFrac)
                .width(maxWidth * (endFrac - startFrac))
                .fillMaxHeight()
                .clip(RoundedCornerShape(5.dp))
                .background(color.copy(alpha = 0.55f))
        )
    }
}
