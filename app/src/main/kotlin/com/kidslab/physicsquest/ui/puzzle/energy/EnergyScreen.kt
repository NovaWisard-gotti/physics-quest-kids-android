package com.kidslab.physicsquest.ui.puzzle.energy

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kidslab.physicsquest.domain.engine.EnergyEngine
import com.kidslab.physicsquest.domain.model.EnergyInput
import com.kidslab.physicsquest.domain.model.EnergyTrackSegment
import com.kidslab.physicsquest.ui.common.AnimatedStarsRow
import com.kidslab.physicsquest.ui.common.FeedbackBanner
import com.kidslab.physicsquest.ui.common.HintButton
import com.kidslab.physicsquest.ui.common.HintNote
import com.kidslab.physicsquest.ui.common.QuestOutlinedButton
import com.kidslab.physicsquest.ui.common.QuestPrimaryButton
import com.kidslab.physicsquest.ui.common.QuestTopBar
import com.kidslab.physicsquest.ui.theme.CoralAccent
import com.kidslab.physicsquest.ui.theme.DaySkyGradient
import com.kidslab.physicsquest.ui.theme.LeafGreen
import com.kidslab.physicsquest.ui.theme.SkyLight
import com.kidslab.physicsquest.ui.theme.SpaceBluePrimary
import com.kidslab.physicsquest.ui.theme.SuccessBg
import com.kidslab.physicsquest.ui.theme.SuccessText
import com.kidslab.physicsquest.ui.theme.TextDark
import com.kidslab.physicsquest.ui.theme.TextMuted
import com.kidslab.physicsquest.ui.theme.WarningBg
import com.kidslab.physicsquest.ui.theme.WarningText

@Composable
fun EnergyScreen(
    viewModel: EnergyViewModel,
    onLevelComplete: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DaySkyGradient)) {
        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Box
        }

        // "Rebote" de la última pieza agregada, para que el recorrido se
        // sienta vivo cada vez que se toca una pieza (y no solo al final).
        val chosenSize = state.chosenOrder.size
        var lastSize by remember { mutableStateOf(chosenSize) }
        val popScale = remember { Animatable(1f) }
        LaunchedEffect(chosenSize) {
            if (chosenSize > lastSize) {
                popScale.snapTo(0.3f)
                popScale.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
            }
            lastSize = chosenSize
        }

        // Vista previa en vivo: se llama al mismo motor de física que usa
        // "Poner en marcha", pero solo para mostrar el resultado sin gastar
        // un intento — así el niño ve de inmediato qué pasaría con el
        // recorrido armado hasta ahora, antes de confirmar nada.
        val livePreview = state.config?.let { config ->
            EnergyEngine.evaluate(EnergyInput(state.chosenOrder.map { EnergyTrackSegment(it.height) }), config)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuestTopBar(title = state.title, onBack = onBack, accentColor = LeafGreen)
            Text(state.instructions, style = MaterialTheme.typography.bodyLarge, color = TextMuted)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SpaceBluePrimary.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "💡 Mientras más alto empieza, más rápido termina: cada bajada suma velocidad y cada subida la gasta.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDark,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth().aspectRatio(1.6f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SkyLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                val config = state.config
                // Alturas y fracciones calculadas una sola vez, fuera del
                // Canvas, para poder reusarlas también en el emoji de la
                // cápsula (que se dibuja como composable, no en el draw scope).
                val heights = if (config != null) {
                    listOf(config.startHeight) + state.chosenOrder.map { it.height } + listOf(config.goalHeight)
                } else emptyList()
                val stepFrac = 1f / (heights.size - 1).coerceAtLeast(1)
                // "Dónde está la cápsula ahora": el último tramo elegido (o el
                // inicio si todavía no se eligió ninguno), no la meta fija.
                val capsuleIndex = state.chosenOrder.size.coerceAtMost((heights.size - 1).coerceAtLeast(0))
                val capsuleFrac = if (heights.isNotEmpty()) {
                    Offset(capsuleIndex * stepFrac, 1f - heights[capsuleIndex].coerceIn(0f, 1.2f) / 1.2f)
                } else Offset(0.05f, 0.5f)

                BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (heights.size < 2) return@Canvas
                        val points = heights.mapIndexed { i, h -> Offset(i * stepFrac * size.width, (1f - h.coerceIn(0f, 1.2f) / 1.2f) * size.height) }
                        for (i in 0 until points.size - 1) {
                            drawLine(SpaceBluePrimary, points[i], points[i + 1], strokeWidth = 8f)
                        }
                        points.forEachIndexed { i, p ->
                            drawCircle(if (i == 0 || i == points.size - 1) CoralAccent else LeafGreen, radius = 12f, center = p)
                        }
                    }
                    // La cápsula "rebota" en el último tramo agregado, para que
                    // cada toque a una pieza se sienta como un cambio real.
                    EmojiMarker(
                        "🛰️", fontSize = 24.sp,
                        nx = capsuleFrac.x, ny = capsuleFrac.y - 0.08f,
                        scale = popScale.value
                    )
                }
            }

            livePreview?.let { preview ->
                val bg = if (preview.success) SuccessBg else WarningBg
                val fg = if (preview.success) SuccessText else WarningText
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = bg), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "${if (preview.success) "✅" else "🤔"} Por ahora: ${preview.feedbackMessage}",
                        style = MaterialTheme.typography.bodySmall,
                        color = fg,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Text("🎢 Piezas disponibles (toca para añadir al recorrido):", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.availablePieces.size) { idx ->
                    val piece = state.availablePieces[idx]
                    val used = state.chosenOrder.any { it.id == piece.id }
                    PieceChip(piece.height, used = used, onClick = { viewModel.addPiece(piece) })
                }
            }

            Text(
                "Tu recorrido: ${state.chosenOrder.joinToString(" → ") { "%.2f".format(it.height) }.ifEmpty { "(vacío)" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDark
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuestOutlinedButton(text = "Quitar última", onClick = viewModel::removeLastPiece, accentColor = SpaceBluePrimary)
                QuestOutlinedButton(text = "Reiniciar recorrido", onClick = viewModel::clearRoute, accentColor = CoralAccent)
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
                        text = "Poner en marcha",
                        icon = "🎢",
                        onClick = viewModel::runTrack,
                        enabled = state.chosenOrder.isNotEmpty(),
                        containerColor = LeafGreen,
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

/** Pieza de pista mostrada como una mini barra (proporcional a su altura) más el número, para que se compare de un vistazo. */
@Composable
private fun PieceChip(height: Float, used: Boolean, onClick: () -> Unit) {
    val barColor = if (used) TextMuted.copy(alpha = 0.4f) else LeafGreen
    Column(
        modifier = Modifier
            .background(if (used) TextMuted.copy(alpha = 0.1f) else LeafGreen.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .then(if (used) Modifier else Modifier.clickable(onClick = onClick))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(20.dp)
                .height((height.coerceIn(0.1f, 1.2f) * 36).dp)
                .background(barColor, RoundedCornerShape(4.dp))
        )
        Text(
            "%.2f".format(height),
            style = MaterialTheme.typography.labelMedium,
            color = if (used) TextMuted else TextDark,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

/** Posiciona un emoji sobre el Canvas usando coordenadas normalizadas (0f..1f), con una escala opcional para animaciones. */
@Composable
private fun BoxWithConstraintsScope.EmojiMarker(
    emoji: String,
    fontSize: TextUnit,
    nx: Float,
    ny: Float,
    scale: Float = 1f
) {
    val halfSize = (fontSize.value / 2).dp
    Text(
        emoji,
        fontSize = fontSize,
        modifier = Modifier
            .offset(x = maxWidth * nx - halfSize, y = maxHeight * ny - halfSize)
            .graphicsLayer { scaleX = scale; scaleY = scale }
    )
}
