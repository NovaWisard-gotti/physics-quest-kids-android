package com.kidslab.physicsquest.ui.puzzle.inclinedplane

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kidslab.physicsquest.ui.common.AnimatedStarsRow
import com.kidslab.physicsquest.ui.common.FeedbackBanner
import com.kidslab.physicsquest.ui.common.HintButton
import com.kidslab.physicsquest.ui.common.HintNote
import com.kidslab.physicsquest.ui.common.QuestPrimaryButton
import com.kidslab.physicsquest.ui.common.QuestTopBar
import com.kidslab.physicsquest.ui.theme.CardSurfaceAlt
import com.kidslab.physicsquest.ui.theme.DaySkyGradient
import com.kidslab.physicsquest.ui.theme.LeafGreen
import com.kidslab.physicsquest.ui.theme.SkyLight
import com.kidslab.physicsquest.ui.theme.TextDark
import com.kidslab.physicsquest.ui.theme.TextMuted
import com.kidslab.physicsquest.ui.theme.VioletAccent
import com.kidslab.physicsquest.ui.theme.WarningText

@Composable
fun InclinedPlaneScreen(
    viewModel: InclinedPlaneViewModel,
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
            QuestTopBar(title = state.title, onBack = onBack, accentColor = VioletAccent)
            Text(state.instructions, style = MaterialTheme.typography.bodyLarge, color = TextMuted)

            val selectedRamp = state.ramps.firstOrNull { it.objectId == state.selectedRampId }
            val loadEmoji = loadEmojiFor(state.title, state.instructions)

            // Vista previa en vivo: la inclinación del dibujo cambia según la
            // rampa elegida, antes de confirmar nada (misma idea que la
            // palanca, que ya se inclina en tiempo real).
            val ratios = state.ramps.map { it.length / it.height.coerceAtLeast(0.01f) }
            val minRatio = ratios.minOrNull() ?: 1f
            val maxRatio = ratios.maxOrNull() ?: 1f
            val ratioSpan = (maxRatio - minRatio).coerceAtLeast(0.01f)
            val horizFrac = selectedRamp?.let {
                0.2f + 0.6f * (((it.length / it.height.coerceAtLeast(0.01f)) - minRatio) / ratioSpan)
            } ?: 0.5f

            Card(
                modifier = Modifier.fillMaxWidth().aspectRatio(1.5f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SkyLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val groundYFrac = 0.85f
                    val baseXFrac = 0.1f
                    val platformTopYFrac = 0.22f
                    val platformXFrac = baseXFrac + horizFrac * (0.92f - baseXFrac)

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val groundY = groundYFrac * size.height
                        val baseX = baseXFrac * size.width
                        val platformTopY = platformTopYFrac * size.height
                        val platformX = platformXFrac * size.width

                        drawLine(TextMuted.copy(alpha = 0.4f), Offset(0.02f * size.width, groundY), Offset(0.98f * size.width, groundY), strokeWidth = 6f)
                        drawLine(VioletAccent, Offset(platformX, platformTopY), Offset(platformX, groundY), strokeWidth = 10f)
                        drawLine(VioletAccent, Offset(platformX - 0.06f * size.width, platformTopY), Offset(platformX + 0.06f * size.width, platformTopY), strokeWidth = 10f)

                        if (selectedRamp != null) {
                            drawLine(LeafGreen, Offset(baseX, groundY), Offset(platformX, platformTopY), strokeWidth = 10f)
                        } else {
                            drawLine(
                                TextMuted.copy(alpha = 0.5f), Offset(baseX, groundY), Offset(platformX, platformTopY),
                                strokeWidth = 6f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f))
                            )
                        }
                    }

                    EmojiMarker(loadEmoji, fontSize = 30.sp, nx = baseXFrac, ny = groundYFrac - 0.07f)
                    EmojiMarker("🚩", fontSize = 24.sp, nx = platformXFrac, ny = platformTopYFrac - 0.08f)
                }
            }

            val config = state.config
            val requiredEffort = selectedRamp?.let { r -> config?.let { it.loadWeight * (r.height / r.length) } }
            val fitsAvailableEffort = requiredEffort != null && config != null && requiredEffort <= config.maxAvailableEffort
            val liveMessage = when {
                selectedRamp == null -> "👉 Elige una rampa para ver si la carga puede subir."
                requiredEffort == null || config == null -> null
                fitsAvailableEffort -> "💪 ¡Con esta rampa la carga sube sin problema!"
                else -> "⚠️ Esta rampa es muy empinada: te va a faltar fuerza."
            }
            liveMessage?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedRamp != null && fitsAvailableEffort) LeafGreen else WarningText,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.ramps.forEach { ramp ->
                    val selected = ramp.objectId == state.selectedRampId
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = selected, onClick = { viewModel.onSelectRamp(ramp.objectId) }),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) VioletAccent.copy(alpha = 0.15f) else CardSurfaceAlt
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 4.dp else 0.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selected,
                                onClick = { viewModel.onSelectRamp(ramp.objectId) },
                                colors = RadioButtonDefaults.colors(selectedColor = VioletAccent)
                            )
                            Column(Modifier.padding(start = 8.dp)) {
                                Text(ramp.label, style = MaterialTheme.typography.titleMedium, color = TextDark, fontWeight = FontWeight.Bold)
                                Text("⛰️ Longitud relativa: ${"%.1f".format(ramp.length)}", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                            }
                        }
                    }
                }
            }

            state.lastResult?.let { result ->
                FeedbackBanner(result.feedbackMessage, result.success, modifier = Modifier.fillMaxWidth())
                if (result.success) AnimatedStarsRow(state.starsEarned)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.levelComplete) {
                    QuestPrimaryButton(text = "Continuar", icon = "➡️", onClick = onLevelComplete, modifier = Modifier.fillMaxWidth())
                } else {
                    QuestPrimaryButton(
                        text = "Subir la carga",
                        icon = "⛰️",
                        onClick = viewModel::tryClimb,
                        enabled = state.selectedRampId != null,
                        containerColor = VioletAccent,
                        modifier = Modifier.fillMaxWidth()
                    )
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
        "barril" in text -> "🛢️"
        "cofre" in text -> "🧳"
        "roca" in text -> "🪨"
        "ingeniera rampa" in text -> "🗿"
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
