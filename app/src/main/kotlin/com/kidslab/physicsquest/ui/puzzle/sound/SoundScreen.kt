package com.kidslab.physicsquest.ui.puzzle.sound

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.common.FeedbackBanner
import com.kidslab.physicsquest.ui.common.HintButton
import com.kidslab.physicsquest.ui.common.QuestTopBar
import com.kidslab.physicsquest.ui.common.StarsRow

@Composable
fun SoundScreen(
    viewModel: SoundViewModel,
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

        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Tu sonido es: ${state.pitchLabel} y ${state.loudnessLabel}", style = MaterialTheme.typography.titleMedium)
                Text("Frecuencia: ${state.frequencyHz.toInt()} Hz  ·  Amplitud: ${(state.amplitude * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Text("Frecuencia (grave ↔ agudo)", style = MaterialTheme.typography.titleMedium)
        Slider(value = state.frequencyHz, onValueChange = viewModel::onFrequencyChange, valueRange = 100f..2000f)

        Text("Amplitud (suave ↔ fuerte)", style = MaterialTheme.typography.titleMedium)
        Slider(value = state.amplitude, onValueChange = viewModel::onAmplitudeChange, valueRange = 0f..1f)

        OutlinedButton(onClick = viewModel::playPreview, modifier = Modifier.fillMaxWidth()) { Text("🔊 Escuchar sonido") }

        state.lastResult?.let { result ->
            FeedbackBanner(result.feedbackMessage, result.success, modifier = Modifier.fillMaxWidth())
            if (result.success) StarsRow(state.starsEarned)
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.levelComplete) {
                Button(onClick = onLevelComplete, modifier = Modifier.fillMaxWidth()) { Text("Continuar") }
            } else {
                Button(onClick = viewModel::submit, modifier = Modifier.fillMaxWidth()) { Text("🚪 Probar la puerta sónica") }
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
