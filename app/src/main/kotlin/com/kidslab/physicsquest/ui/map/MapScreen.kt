package com.kidslab.physicsquest.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.common.StarsRow

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onOpenWorld: (Long) -> Unit,
    onOpenInventory: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Mapa de la aventura", style = MaterialTheme.typography.headlineMedium)
        Text("⭐ Estrellas totales: ${state.totalStars}", style = MaterialTheme.typography.titleMedium)

        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.worlds) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.unlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    ),
                    onClick = { if (item.unlocked) onOpenWorld(item.world.id) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Mundo ${item.world.order}: ${item.world.title}", style = MaterialTheme.typography.titleLarge)
                        Text(item.world.subtitle, style = MaterialTheme.typography.bodyMedium)
                        Text(item.world.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                        StarsRow(minOf(item.starsEarned, 3), modifier = Modifier.padding(top = 8.dp))
                        Text("${item.starsEarned} / ${item.maxStars} estrellas del mundo", style = MaterialTheme.typography.bodyMedium)
                        if (!item.unlocked) {
                            Text(
                                "🔒 Necesitas ${item.world.starsRequiredToUnlock} estrellas en el mundo anterior",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth(), onClick = onOpenInventory) {
                    Text("🎒 Inventario y logros", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
