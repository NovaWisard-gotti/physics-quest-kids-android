package com.kidslab.physicsquest.ui.boss

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.common.QuestTopBar

@Composable
fun BossScreen(
    viewModel: BossViewModel,
    onStartChallenge: (Long) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val boss = state.boss ?: return

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        QuestTopBar(title = "👑 Jefe científico", onBack = onBack)
        Text(boss.title, style = MaterialTheme.typography.titleLarge)

        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f))) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(boss.scientistName, style = MaterialTheme.typography.titleMedium)
                Text(boss.introDialogue, style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic)
            }
        }

        Text("Este desafío combina: ${boss.mixedConceptsDescription}", style = MaterialTheme.typography.bodyLarge)

        Button(onClick = { onStartChallenge(boss.basePuzzleLevelId) }, modifier = Modifier.fillMaxWidth()) {
            Text("Comenzar el desafío")
        }
    }
}
