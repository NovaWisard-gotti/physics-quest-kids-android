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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.common.CircleEmblem
import com.kidslab.physicsquest.ui.common.QuestTopBar
import com.kidslab.physicsquest.ui.common.StarsRow
import com.kidslab.physicsquest.ui.theme.CardSurfaceAlt
import com.kidslab.physicsquest.ui.theme.DaySkyGradient
import com.kidslab.physicsquest.ui.theme.SunshineYellow
import com.kidslab.physicsquest.ui.theme.SunshineYellowLight
import com.kidslab.physicsquest.ui.theme.TextDark
import com.kidslab.physicsquest.ui.theme.TextMuted
import com.kidslab.physicsquest.ui.theme.worldBrush
import com.kidslab.physicsquest.ui.theme.worldColor

@Composable
fun WorldLevelsScreen(
    viewModel: WorldLevelsViewModel,
    onOpenLevel: (Long) -> Unit,
    onOpenBoss: (Long) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val worldOrder = state.world?.order ?: 1
    val accent = worldColor(worldOrder)

    Box(modifier = Modifier.fillMaxSize().background(DaySkyGradient)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            QuestTopBar(
                title = state.world?.let { "Mundo ${it.order}: ${it.title}" } ?: "",
                onBack = onBack,
                accentColor = accent
            )
            state.world?.let {
                Text(
                    it.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
            }

            if (state.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                return@Column
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(state.levels) { index, item ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(280, delayMillis = index * 50)) +
                            slideInVertically(tween(280, delayMillis = index * 50)) { it / 4 }
                    ) {
                        if (item.isBoss) {
                            BossLevelCard(accent = accent, onClick = { onOpenBoss(item.level.id) })
                        } else {
                            LevelCard(
                                item = item,
                                accentBrush = worldBrush(worldOrder),
                                onClick = { onOpenLevel(item.level.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelCard(item: LevelListItem, accentBrush: Brush, onClick: () -> Unit) {
    val completed = item.starsEarned > 0
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (completed) CardSurfaceAlt else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircleEmblem(brush = accentBrush, size = 40.dp) {
                    Text("${item.level.levelNumberInWorld}", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                }
                Text(
                    item.level.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDark,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            StarsRow(item.starsEarned)
        }
    }
}

@Composable
private fun BossLevelCard(accent: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.92f), Color(0xFF2A1F55))))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(SunshineYellow, RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👑", style = MaterialTheme.typography.titleLarge)
                }
                Column(Modifier.padding(start = 12.dp)) {
                    Text("Jefe del mundo", style = MaterialTheme.typography.bodySmall, color = SunshineYellowLight)
                    Text("¡Desafío final!", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
            }
            Text("⚔️", style = MaterialTheme.typography.headlineSmall)
        }
    }
}
