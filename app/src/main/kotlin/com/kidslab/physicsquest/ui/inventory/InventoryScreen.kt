package com.kidslab.physicsquest.ui.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

@Composable
fun InventoryScreen(viewModel: InventoryViewModel) {
    val state by viewModel.uiState.collectAsState()

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("🎒 Inventario y logros", style = MaterialTheme.typography.headlineMedium) }

        item { Text("Insignias (${state.badges.count { it.earned }}/${state.badges.size})", style = MaterialTheme.typography.titleLarge) }
        items(state.badges) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.earned) MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text((if (item.earned) "🏅 " else "🔒 ") + item.badge.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (item.earned) item.badge.description else item.badge.criteriaDescription,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item { Text("Tarjetas de concepto (${state.conceptCards.count { it.unlocked }}/${state.conceptCards.size})", style = MaterialTheme.typography.titleLarge) }
        items(state.conceptCards) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.unlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(Modifier.padding(12.dp)) {
                    if (item.unlocked) {
                        Text(item.card.title, style = MaterialTheme.typography.titleMedium)
                        Text(item.card.shortExplanation, style = MaterialTheme.typography.bodyMedium)
                        Text("Ejemplo: ${item.card.everydayExample}", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        Text("🔒 Tarjeta bloqueada", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
