package com.kidslab.physicsquest.ui.puzzle.lever

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
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.common.FeedbackBanner
import com.kidslab.physicsquest.ui.common.HintButton
import com.kidslab.physicsquest.ui.common.QuestTopBar
import com.kidslab.physicsquest.ui.common.StarsRow
import com.kidslab.physicsquest.ui.theme.CoralAccent
import com.kidslab.physicsquest.ui.theme.SpaceBluePrimary

@Composable
fun LeverScreen(
    viewModel: LeverViewModel,
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
            modifier = Modifier.fillMaxWidth().aspectRatio(1.6f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF2FF))
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                val barY = size.height * 0.55f
                val leftX = size.width * 0.08f
                val rightX = size.width * 0.92f
                val fulcrumX = leftX + (rightX - leftX) * state.fulcrumPosition

                // Barra
                drawLine(SpaceBluePrimary, Offset(leftX, barY), Offset(rightX, barY), strokeWidth = 10f)
                // Punto de apoyo (triángulo simplificado como círculo)
                drawCircle(CoralAccent, radius = 16f, center = Offset(fulcrumX, barY + 10f))
                // Carga (izquierda)
                drawCircle(SpaceBluePrimary, radius = 20f, center = Offset(leftX, barY - 20f))
                // Explorador (derecha)
                drawCircle(CoralAccent, radius = 16f, center = Offset(rightX, barY - 16f))
            }
        }

        Text("Posición del punto de apoyo: ${(state.fulcrumPosition * 100).toInt()}%", style = MaterialTheme.typography.titleMedium)
        Slider(value = state.fulcrumPosition, onValueChange = viewModel::onFulcrumChange, valueRange = 0.05f..0.95f)

        Text("Fuerza aplicada: ${(state.effortForce * 100).toInt()}%", style = MaterialTheme.typography.titleMedium)
        Slider(value = state.effortForce, onValueChange = viewModel::onEffortChange, valueRange = 0f..1f)

        state.lastResult?.let { result ->
            FeedbackBanner(result.feedbackMessage, result.success, modifier = Modifier.fillMaxWidth())
            if (result.success) StarsRow(state.starsEarned)
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.levelComplete) {
                Button(onClick = onLevelComplete, modifier = Modifier.fillMaxWidth()) { Text("Continuar") }
            } else {
                Button(onClick = viewModel::tryLift, modifier = Modifier.fillMaxWidth()) { Text("⚙️ Levantar carga") }
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
