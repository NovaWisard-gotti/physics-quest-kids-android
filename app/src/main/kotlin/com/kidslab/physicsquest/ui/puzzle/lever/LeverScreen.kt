package com.kidslab.physicsquest.ui.puzzle.lever

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kidslab.physicsquest.domain.engine.LeverEngine
import com.kidslab.physicsquest.ui.common.AnimatedStarsRow
import com.kidslab.physicsquest.ui.common.FeedbackBanner
import com.kidslab.physicsquest.ui.common.HintButton
import com.kidslab.physicsquest.ui.common.HintNote
import com.kidslab.physicsquest.ui.common.QuestPrimaryButton
import com.kidslab.physicsquest.ui.common.QuestTopBar
import com.kidslab.physicsquest.ui.theme.CoralAccent
import com.kidslab.physicsquest.ui.theme.DaySkyGradient
import com.kidslab.physicsquest.ui.theme.LeafGreen
import com.kidslab.physicsquest.ui.theme.SkyLight
import com.kidslab.physicsquest.ui.theme.SpaceBluePrimary
import com.kidslab.physicsquest.ui.theme.TextMuted
import kotlin.math.sin

@Composable
fun LeverScreen(
    viewModel: LeverViewModel,
    onLevelComplete: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DaySkyGradient)) {
        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            QuestTopBar(title = state.title, onBack = onBack, accentColor = SpaceBluePrimary)
            Text(state.instructions, style = MaterialTheme.typography.bodyLarge, color = TextMuted)

            // Requerido en vivo (sin necesitar presionar el botón) para que el
            // niño vea de inmediato si la palanca "ya se levantaría": la barra
            // se inclina de verdad según la fuerza y el punto de apoyo actuales.
            val config = state.config
            val requiredEffort = config?.let { LeverEngine.requiredEffort(state.fulcrumPosition, it) }
            val balance = when {
                config == null -> 0f
                requiredEffort == null -> -1f
                else -> (state.effortForce - requiredEffort).coerceIn(-1f, 1f)
            }
            val tiltRad = (balance * 0.4f).coerceIn(-0.35f, 0.35f)

            // Geometría de la barra en fracciones (0f..1f), calculada una sola
            // vez fuera del Canvas para que tanto el dibujo como los emojis
            // superpuestos usen exactamente los mismos puntos.
            val leftXFrac = 0.08f
            val rightXFrac = 0.92f
            val barYFrac = 0.55f
            val fulcrumXFrac = leftXFrac + (rightXFrac - leftXFrac) * state.fulcrumPosition
            val leftYFrac = barYFrac - (fulcrumXFrac - leftXFrac) * sin(tiltRad)
            val rightYFrac = barYFrac + (rightXFrac - fulcrumXFrac) * sin(tiltRad)

            Card(
                modifier = Modifier.fillMaxWidth().aspectRatio(1.6f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SkyLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barY = barYFrac * size.height
                        val leftX = leftXFrac * size.width
                        val rightX = rightXFrac * size.width
                        val fulcrumX = fulcrumXFrac * size.width
                        val leftY = leftYFrac * size.height
                        val rightY = rightYFrac * size.height

                        // Cuña de apoyo (triángulo), fija bajo el punto de apoyo.
                        val wedge = Path().apply {
                            moveTo(fulcrumX - 20f, barY + 34f)
                            lineTo(fulcrumX + 20f, barY + 34f)
                            lineTo(fulcrumX, barY + 6f)
                            close()
                        }
                        drawPath(wedge, CoralAccent)

                        drawLine(SpaceBluePrimary, Offset(leftX, leftY), Offset(rightX, rightY), strokeWidth = 14f)
                    }

                    // Emojis en vez de círculos de color: la carga se ve como lo
                    // que describe cada nivel (piedra, baúl, metal...), no
                    // siempre la misma caja.
                    EmojiMarker(loadEmojiFor(state.title, state.instructions), fontSize = 28.sp, nx = leftXFrac, ny = leftYFrac - 0.06f)
                    EmojiMarker("🧑‍🚀", fontSize = 26.sp, nx = rightXFrac, ny = rightYFrac - 0.06f)
                }
            }

            val liveMessage = when {
                config == null -> null
                requiredEffort == null -> "🤔 El punto de apoyo está muy cerca de un extremo: muévelo hacia el centro."
                balance >= 0f -> "💪 ¡Con esta posición y fuerza, la carga se levanta!"
                else -> "⚖️ Todavía falta fuerza (o acercar más el apoyo a la caja)."
            }
            liveMessage?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = if (balance >= 0f) LeafGreen else TextMuted, fontWeight = FontWeight.SemiBold)
            }

            Text("⚖️ Posición del punto de apoyo: ${(state.fulcrumPosition * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Slider(
                value = state.fulcrumPosition,
                onValueChange = viewModel::onFulcrumChange,
                valueRange = 0.05f..0.95f,
                colors = SliderDefaults.colors(thumbColor = SpaceBluePrimary, activeTrackColor = SpaceBluePrimary)
            )

            Text("💪 Fuerza aplicada: ${(state.effortForce * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Slider(
                value = state.effortForce,
                onValueChange = viewModel::onEffortChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(thumbColor = CoralAccent, activeTrackColor = CoralAccent)
            )

            state.lastResult?.let { result ->
                FeedbackBanner(result.feedbackMessage, result.success, modifier = Modifier.fillMaxWidth())
                if (result.success) AnimatedStarsRow(state.starsEarned)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.levelComplete) {
                    QuestPrimaryButton(text = "Continuar", icon = "➡️", onClick = onLevelComplete, modifier = Modifier.fillMaxWidth())
                } else {
                    QuestPrimaryButton(text = "Levantar carga", icon = "⚙️", onClick = viewModel::tryLift, modifier = Modifier.fillMaxWidth())
                }
                HintButton(
                    available = state.hintAvailable,
                    hintText = state.revealedHints.lastOrNull(),
                    onRequestHint = viewModel::requestHint,
                    modifier = Modifier.fillMaxWidth()
                )
                state.revealedHints.forEach { hint -> HintNote(hint) }
            }
        }
    }
}

/** Elige un emoji acorde a lo que describe el nivel, en vez de usar siempre la misma caja. */
private fun loadEmojiFor(title: String, instructions: String): String {
    val text = "$title $instructions".lowercase()
    return when {
        "piedra" in text -> "🪨"
        "baúl" in text -> "🧳"
        "metal" in text -> "🧱"
        "torque" in text -> "⚙️"
        else -> "📦"
    }
}

/** Posiciona un emoji sobre el Canvas usando coordenadas normalizadas (0f..1f), igual que el dibujo. */
@Composable
private fun BoxWithConstraintsScope.EmojiMarker(
    emoji: String,
    fontSize: TextUnit,
    nx: Float,
    ny: Float
) {
    val halfSize = (fontSize.value / 2).dp
    Text(
        emoji,
        fontSize = fontSize,
        modifier = Modifier.offset(x = maxWidth * nx - halfSize, y = maxHeight * ny - halfSize)
    )
}
