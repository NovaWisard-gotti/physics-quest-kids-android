package com.kidslab.physicsquest.ui.puzzle.inclinedplane

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kidslab.physicsquest.ui.common.FeedbackBanner
import com.kidslab.physicsquest.ui.common.HintButton
import com.kidslab.physicsquest.ui.common.StarsRow
import androidx.compose.ui.unit.dp

@Composable
fun InclinedPlaneScreen(
    viewModel: InclinedPlaneViewModel,
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
        Text(state.title, style = MaterialTheme.typography.headlineMedium)
        Text(state.instructions, style = MaterialTheme.typography.bodyLarge)

        state.ramps.forEach { ramp ->
            val selected = ramp.objectId == state.selectedRampId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = selected, onClick = { viewModel.onSelectRamp(ramp.objectId) }),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selected, onClick = { viewModel.onSelectRamp(ramp.objectId) })
                    Column(Modifier.padding(start = 8.dp)) {
                        Text(ramp.label, style = MaterialTheme.typography.titleMedium)
                        Text("Longitud relativa: ${"%.1f".format(ramp.length)}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
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
                    onClick = viewModel::tryClimb,
                    enabled = state.selectedRampId != null,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("⛰️ Subir la carga") }
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
