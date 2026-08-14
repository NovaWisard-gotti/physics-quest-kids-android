package com.kidslab.physicsquest.ui.puzzle.trajectory

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.common.FeedbackBanner
import com.kidslab.physicsquest.ui.common.HintButton
import com.kidslab.physicsquest.ui.common.QuestTopBar
import com.kidslab.physicsquest.ui.common.StarsRow
import com.kidslab.physicsquest.ui.theme.CoralAccent
import com.kidslab.physicsquest.ui.theme.LeafGreen
import com.kidslab.physicsquest.ui.theme.SpaceBluePrimary

@Composable
fun TrajectoryScreen(
    viewModel: TrajectoryViewModel,
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
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        QuestTopBar(title = state.title, onBack = onBack)
        Text(state.instructions, style = MaterialTheme.typography.bodyLarge)

        Card(
            modifier = Modifier.fillMaxWidth().aspectRatio(1.3f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2FF))
        ) {
            val config = state.config
            Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                if (config == null) return@Canvas
                fun toOffset(nx: Float, ny: Float) = Offset(nx * size.width, ny * size.height)

                // Trayectoria de vista previa
                val pathPoints = state.previewPath.map { toOffset(it.first, it.second) }
                for (i in 0 until pathPoints.size - 1) {
                    drawLine(SpaceBluePrimary, pathPoints[i], pathPoints[i + 1], strokeWidth = 5f)
                }

                // Meta
                drawCircle(LeafGreen, radius = config.toleranceRadius * size.minDimension, center = toOffset(config.targetX, config.targetY), style = Stroke(width = 4f))
                drawCircle(LeafGreen.copy(alpha = 0.25f), radius = config.toleranceRadius * size.minDimension, center = toOffset(config.targetX, config.targetY))

                // Obstáculos
                config.obstacles.forEach { (ox, oy) ->
                    drawCircle(CoralAccent, radius = config.obstacleRadius * size.minDimension, center = toOffset(ox, oy))
                }

                // Punto de lanzamiento
                drawCircle(SpaceBluePrimary, radius = 14f, center = toOffset(config.launchX, config.launchY))
            }
        }

        Text("Ángulo: ${state.angleDegrees.toInt()}°", style = MaterialTheme.typography.titleMedium)
        Slider(value = state.angleDegrees, onValueChange = viewModel::onAngleChange, valueRange = 5f..175f)

        Text("Fuerza: ${(state.force * 100).toInt()}%", style = MaterialTheme.typography.titleMedium)
        Slider(value = state.force, onValueChange = viewModel::onForceChange, valueRange = 0.1f..1f)

        state.lastResult?.let { result ->
            FeedbackBanner(result.feedbackMessage, result.success, modifier = Modifier.fillMaxWidth())
            if (result.success) {
                StarsRow(state.starsEarned)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.levelComplete) {
                Button(onClick = onLevelComplete, modifier = Modifier.fillMaxWidth()) { Text("Continuar") }
            } else {
                Button(onClick = viewModel::launchBall, modifier = Modifier.fillMaxWidth()) { Text("🚀 Lanzar") }
            }
            HintButton(
                available = state.hintAvailable,
                hintText = state.revealedHints.lastOrNull(),
                onRequestHint = viewModel::requestHint,
                modifier = Modifier.fillMaxWidth()
            )
            state.revealedHints.forEach { hint ->
                Text("💡 $hint", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
