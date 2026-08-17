package com.kidslab.physicsquest.ui.puzzle.trajectory

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

@Composable
fun TrajectoryScreen(
    viewModel: TrajectoryViewModel,
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
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            QuestTopBar(title = state.title, onBack = onBack, accentColor = SpaceBluePrimary)
            Text(state.instructions, style = MaterialTheme.typography.bodyLarge, color = TextMuted)

            Card(
                modifier = Modifier.fillMaxWidth().aspectRatio(1.3f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SkyLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                val config = state.config
                Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    if (config == null) return@Canvas
                    fun toOffset(nx: Float, ny: Float) = Offset(nx * size.width, ny * size.height)

                    val pathPoints = state.previewPath.map { toOffset(it.first, it.second) }
                    for (i in 0 until pathPoints.size - 1) {
                        drawLine(SpaceBluePrimary, pathPoints[i], pathPoints[i + 1], strokeWidth = 6f)
                    }

                    drawCircle(LeafGreen, radius = config.toleranceRadius * size.minDimension, center = toOffset(config.targetX, config.targetY), style = Stroke(width = 5f))
                    drawCircle(LeafGreen.copy(alpha = 0.25f), radius = config.toleranceRadius * size.minDimension, center = toOffset(config.targetX, config.targetY))

                    config.obstacles.forEach { (ox, oy) ->
                        drawCircle(CoralAccent, radius = config.obstacleRadius * size.minDimension, center = toOffset(ox, oy))
                    }

                    drawCircle(SpaceBluePrimary, radius = 16f, center = toOffset(config.launchX, config.launchY))
                }
            }

            Text("🎯 Ángulo: ${state.angleDegrees.toInt()}°", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Slider(
                value = state.angleDegrees,
                onValueChange = viewModel::onAngleChange,
                valueRange = 5f..175f,
                colors = SliderDefaults.colors(thumbColor = SpaceBluePrimary, activeTrackColor = SpaceBluePrimary)
            )

            Text("💥 Fuerza: ${(state.force * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Slider(
                value = state.force,
                onValueChange = viewModel::onForceChange,
                valueRange = 0.1f..1f,
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
                    QuestPrimaryButton(text = "Lanzar", icon = "🚀", onClick = viewModel::launchBall, modifier = Modifier.fillMaxWidth())
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
