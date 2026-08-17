package com.kidslab.physicsquest.ui.puzzle.energy

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.kidslab.physicsquest.ui.theme.TextDark
import com.kidslab.physicsquest.ui.theme.TextMuted

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
                modifier = Modifier.fillMaxWidth().aspectRatio(1.6f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SkyLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                val config = state.config
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    if (config == null) return@Canvas
                    val heights = (listOf(config.startHeight) + state.chosenOrder.map { it.height } + listOf(config.goalHeight))
                    val stepX = size.width / (heights.size - 1).coerceAtLeast(1)
                    val points = heights.mapIndexed { i, h -> Offset(i * stepX, (1f - h.coerceIn(0f, 1.2f) / 1.2f) * size.height) }
                    for (i in 0 until points.size - 1) {
                        drawLine(SpaceBluePrimary, points[i], points[i + 1], strokeWidth = 8f)
                    }
                    points.forEachIndexed { i, p ->
                        drawCircle(if (i == 0 || i == points.size - 1) CoralAccent else LeafGreen, radius = 12f, center = p)
                    }
                }
            }

            Text("🎢 Piezas disponibles (toca para añadir al recorrido):", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.availablePieces.size) { idx ->
                    val piece = state.availablePieces[idx]
                    val used = state.chosenOrder.any { it.id == piece.id }
                    AssistChip(
                        onClick = { viewModel.addPiece(piece) },
                        enabled = !used,
                        label = { Text("Altura ${"%.2f".format(piece.height)}", fontWeight = FontWeight.Bold) },
                        shape = RoundedCornerShape(50),
                        colors = AssistChipDefaults.assistChipColors(containerColor = LeafGreen.copy(alpha = 0.15f))
                    )
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
