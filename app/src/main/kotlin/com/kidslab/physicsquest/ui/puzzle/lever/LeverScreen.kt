package com.kidslab.physicsquest.ui.puzzle.lever

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
import com.kidslab.physicsquest.ui.theme.SkyLight
import com.kidslab.physicsquest.ui.theme.SpaceBluePrimary
import com.kidslab.physicsquest.ui.theme.TextMuted

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
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            QuestTopBar(title = state.title, onBack = onBack, accentColor = SpaceBluePrimary)
            Text(state.instructions, style = MaterialTheme.typography.bodyLarge, color = TextMuted)

            Card(
                modifier = Modifier.fillMaxWidth().aspectRatio(1.6f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SkyLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val barY = size.height * 0.55f
                    val leftX = size.width * 0.08f
                    val rightX = size.width * 0.92f
                    val fulcrumX = leftX + (rightX - leftX) * state.fulcrumPosition

                    drawLine(SpaceBluePrimary, Offset(leftX, barY), Offset(rightX, barY), strokeWidth = 12f)
                    drawCircle(CoralAccent, radius = 18f, center = Offset(fulcrumX, barY + 12f))
                    drawCircle(SpaceBluePrimary, radius = 22f, center = Offset(leftX, barY - 22f))
                    drawCircle(CoralAccent, radius = 18f, center = Offset(rightX, barY - 18f))
                }
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
