package com.kidslab.physicsquest.ui.puzzle.sound

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.common.AnimatedStarsRow
import com.kidslab.physicsquest.ui.common.FeedbackBanner
import com.kidslab.physicsquest.ui.common.HintButton
import com.kidslab.physicsquest.ui.common.HintNote
import com.kidslab.physicsquest.ui.common.QuestOutlinedButton
import com.kidslab.physicsquest.ui.common.QuestPrimaryButton
import com.kidslab.physicsquest.ui.common.QuestTopBar
import com.kidslab.physicsquest.ui.common.RangeHint
import com.kidslab.physicsquest.ui.theme.CoralAccent
import com.kidslab.physicsquest.ui.theme.DaySkyGradient
import com.kidslab.physicsquest.ui.theme.LeafGreen
import com.kidslab.physicsquest.ui.theme.SunshineYellow
import com.kidslab.physicsquest.ui.theme.SunshineYellowLight
import com.kidslab.physicsquest.ui.theme.TextDark
import com.kidslab.physicsquest.ui.theme.TextMuted

@Composable
fun SoundScreen(
    viewModel: SoundViewModel,
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
            QuestTopBar(title = state.title, onBack = onBack, accentColor = CoralAccent)
            Text(state.instructions, style = MaterialTheme.typography.bodyLarge, color = TextMuted)

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SunshineYellowLight.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "🔊 Tu sonido es: ${state.pitchLabel} y ${state.loudnessLabel}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Frecuencia: ${state.frequencyHz.toInt()} Hz  ·  Amplitud: ${(state.amplitude * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }

            Text(
                "🎯 La franja marcada abajo de cada control es la zona que abre la puerta.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )

            Text("🎵 Frecuencia (grave ↔ agudo)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Slider(
                value = state.frequencyHz,
                onValueChange = viewModel::onFrequencyChange,
                valueRange = 100f..2000f,
                colors = SliderDefaults.colors(thumbColor = CoralAccent, activeTrackColor = CoralAccent)
            )
            state.config?.let { config ->
                RangeHint(
                    valueRange = 100f..2000f,
                    targetRange = config.minFrequencyHz..config.maxFrequencyHz,
                    color = CoralAccent,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Text("📢 Amplitud (suave ↔ fuerte)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Slider(
                value = state.amplitude,
                onValueChange = viewModel::onAmplitudeChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(thumbColor = SunshineYellow, activeTrackColor = SunshineYellow)
            )
            state.config?.let { config ->
                RangeHint(
                    valueRange = 0f..1f,
                    targetRange = config.minAmplitude..config.maxAmplitude,
                    color = LeafGreen
                )
            }

            QuestOutlinedButton(
                text = "Escuchar sonido",
                icon = "🔊",
                onClick = viewModel::playPreview,
                accentColor = CoralAccent,
                modifier = Modifier.fillMaxWidth()
            )

            state.lastResult?.let { result ->
                FeedbackBanner(result.feedbackMessage, result.success, modifier = Modifier.fillMaxWidth())
                if (result.success) AnimatedStarsRow(state.starsEarned)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.levelComplete) {
                    QuestPrimaryButton(text = "Continuar", icon = "➡️", onClick = onLevelComplete, modifier = Modifier.fillMaxWidth())
                } else {
                    QuestPrimaryButton(
                        text = "Probar la puerta sónica",
                        icon = "🚪",
                        onClick = viewModel::submit,
                        containerColor = CoralAccent,
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
