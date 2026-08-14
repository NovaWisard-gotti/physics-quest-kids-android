package com.kidslab.physicsquest.ui.puzzle.energy

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.common.FeedbackBanner
import com.kidslab.physicsquest.ui.common.HintButton
import com.kidslab.physicsquest.ui.common.QuestTopBar
import com.kidslab.physicsquest.ui.common.StarsRow
import com.kidslab.physicsquest.ui.theme.CoralAccent
import com.kidslab.physicsquest.ui.theme.SpaceBluePrimary

@Composable
fun EnergyScreen(
    viewModel: EnergyViewModel,
    onLevelComplete: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    if (state.loading) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuestTopBar(title = state.title, onBack = onBack)
        Text(state.instructions, style = MaterialTheme.typography.bodyLarge)

        Card(
            modifier = Modifier.fillMaxWidth().aspectRatio(1.6f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2FF))
        ) {
            val config = state.config
            Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                if (config == null) return@Canvas
                val heights = (listOf(config.startHeight) + state.chosenOrder.map { it.height } + listOf(config.goalHeight))
                val stepX = size.width / (heights.size - 1).coerceAtLeast(1)
                val points = heights.mapIndexed { i, h -> Offset(i * stepX, (1f - h.coerceIn(0f, 1.2f) / 1.2f) * size.height) }
                for (i in 0 until points.size - 1) {
                    drawLine(SpaceBluePrimary, points[i], points[i + 1], strokeWidth = 6f)
                }
                points.forEachIndexed { i, p ->
                    drawCircle(if (i == 0 || i == points.size - 1) CoralAccent else SpaceBluePrimary, radius = 10f, center = p)
                }
            }
        }

        Text("Piezas disponibles (toca para añadir al recorrido):", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.availablePieces.size) { idx ->
                val piece = state.availablePieces[idx]
                val used = state.chosenOrder.any { it.id == piece.id }
                AssistChip(
                    onClick = { viewModel.addPiece(piece) },
                    enabled = !used,
                    label = { Text("Altura ${"%.2f".format(piece.height)}") }
                )
            }
        }

        Text("Tu recorrido: ${state.chosenOrder.joinToString(" → ") { "%.2f".format(it.height) }.ifEmpty { "(vacío)" }}")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = viewModel::removeLastPiece) { Text("Quitar última") }
            OutlinedButton(onClick = viewModel::clearRoute) { Text("Reiniciar recorrido") }
        }

        state.lastResult?.let { result ->
            FeedbackBanner(result.feedbackMessage, result.success, modifier = Modifier.fillMaxWidth())
            if (result.success) StarsRow(state.starsEarned)
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.levelComplete) {
                Button(onClick = onLevelComplete, modifier = Modifier.fillMaxWidth()) { Text("Continuar") }
            } else {
                Button(
                    onClick = viewModel::runTrack,
                    enabled = state.chosenOrder.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("🎢 Poner en marcha") }
            }
            HintButton(
                available = state.hintAvailable,
                hintText = state.revealedHints.lastOrNull(),
                onRequestHint = viewModel::requestHint,
                modifier = Modifier.fillMaxWidth()
            )
            state.revealedHints.forEach { hint -> Text("💡 $hint", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
