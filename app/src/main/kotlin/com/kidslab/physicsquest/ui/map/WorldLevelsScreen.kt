package com.kidslab.physicsquest.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.common.StarsRow

@Composable
fun WorldLevelsScreen(
    viewModel: WorldLevelsViewModel,
    onOpenLevel: (Long) -> Unit,
    onOpenBoss: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(state.world?.let { "Mundo ${it.order}: ${it.title}" } ?: "", style = MaterialTheme.typography.headlineMedium)
        state.world?.let { Text(it.description, style = MaterialTheme.typography.bodyLarge) }

        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.levels) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.isBoss) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                    ),
                    onClick = { if (item.isBoss) onOpenBoss(item.level.id) else onOpenLevel(item.level.id) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                (if (item.isBoss) "👑 " else "Nivel ${item.level.levelNumberInWorld}: ") + item.level.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        StarsRow(item.starsEarned)
                    }
                }
            }
        }
    }
}
