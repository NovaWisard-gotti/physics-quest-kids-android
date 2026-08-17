package com.kidslab.physicsquest.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.common.CircleEmblem
import com.kidslab.physicsquest.ui.common.StarsRow
import com.kidslab.physicsquest.ui.theme.CardSurfaceAlt
import com.kidslab.physicsquest.ui.theme.DaySkyGradient
import com.kidslab.physicsquest.ui.theme.LockedGray
import com.kidslab.physicsquest.ui.theme.TextDark
import com.kidslab.physicsquest.ui.theme.TextMuted
import com.kidslab.physicsquest.ui.theme.VioletAccent
import com.kidslab.physicsquest.ui.theme.VioletAccentLight
import com.kidslab.physicsquest.ui.theme.worldBrush
import com.kidslab.physicsquest.ui.theme.worldColor

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onOpenWorld: (Long) -> Unit,
    onOpenInventory: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DaySkyGradient)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Mapa de la aventura", style = MaterialTheme.typography.headlineLarge, color = TextDark)
            Row(
                modifier = Modifier
                    .padding(top = 6.dp, bottom = 16.dp)
                    .background(Color.White, RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⭐", style = MaterialTheme.typography.titleMedium)
                Text(
                    " ${state.totalStars} estrellas totales",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                return@Column
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                itemsIndexed(state.worlds) { index, item ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(320, delayMillis = index * 60)) +
                            slideInVertically(tween(320, delayMillis = index * 60)) { it / 5 }
                    ) {
                        WorldCard(item = item, onClick = { if (item.unlocked) onOpenWorld(item.world.id) })
                    }
                }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        onClick = onOpenInventory
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Brush.horizontalGradient(listOf(VioletAccent, VioletAccentLight)))
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎒", style = MaterialTheme.typography.headlineMedium)
                                Text(
                                    "Inventario y logros",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                            Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorldCard(item: WorldMapItem, onClick: () -> Unit) {
    val accentBrush = if (item.unlocked) worldBrush(item.world.order) else Brush.linearGradient(listOf(LockedGray, LockedGray))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.unlocked) 5.dp else 1.dp),
        onClick = onClick
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            CircleEmblem(brush = accentBrush, size = 52.dp) {
                if (item.unlocked) {
                    Text("${item.world.order}", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                } else {
                    Icon(Icons.Filled.Lock, contentDescription = "Mundo bloqueado", tint = Color.White, modifier = Modifier.size(22.dp))
                }
            }
            Column(Modifier.padding(start = 14.dp).weight(1f)) {
                Text(
                    item.world.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (item.unlocked) TextDark else TextMuted
                )
                Text(
                    item.world.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (item.unlocked) worldColor(item.world.order) else TextMuted,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    item.world.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StarsRow(minOf(item.starsEarned, 3))
                    Text(
                        " ${item.starsEarned}/${item.maxStars} ⭐ del mundo",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                if (!item.unlocked) {
                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .background(CardSurfaceAlt, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "🔒 Necesitas ${item.world.starsRequiredToUnlock} estrellas en el mundo anterior",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}
