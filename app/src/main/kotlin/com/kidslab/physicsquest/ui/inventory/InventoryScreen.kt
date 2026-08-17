package com.kidslab.physicsquest.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.common.QuestTopBar
import com.kidslab.physicsquest.ui.common.SectionLabel
import com.kidslab.physicsquest.ui.theme.CardSurfaceAlt
import com.kidslab.physicsquest.ui.theme.DaySkyGradient
import com.kidslab.physicsquest.ui.theme.LockedGray
import com.kidslab.physicsquest.ui.theme.SpaceBluePrimary
import com.kidslab.physicsquest.ui.theme.SpaceBlueLight
import com.kidslab.physicsquest.ui.theme.SunshineYellow
import com.kidslab.physicsquest.ui.theme.SunshineYellowLight
import com.kidslab.physicsquest.ui.theme.TextDark
import com.kidslab.physicsquest.ui.theme.TextMuted

@Composable
fun InventoryScreen(viewModel: InventoryViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DaySkyGradient)) {
        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Box
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { QuestTopBar(title = "🎒 Inventario y logros", onBack = onBack) }

            item {
                SectionLabel(
                    "Insignias (${state.badges.count { it.earned }}/${state.badges.size})",
                    accent = SunshineYellow,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(state.badges.chunked(2)) { rowBadges ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    rowBadges.forEach { badgeItem -> BadgeTile(badgeItem, modifier = Modifier.weight(1f)) }
                    if (rowBadges.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }

            item {
                SectionLabel(
                    "Tarjetas de concepto (${state.conceptCards.count { it.unlocked }}/${state.conceptCards.size})",
                    accent = SpaceBluePrimary,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            items(state.conceptCards) { item -> ConceptCardTile(item) }
        }
    }
}

@Composable
private fun BadgeTile(item: BadgeUi, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.earned) 5.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (item.earned) Brush.linearGradient(listOf(SunshineYellow, SunshineYellowLight)) else Brush.linearGradient(listOf(LockedGray, LockedGray)),
                        RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(if (item.earned) "🏅" else "🔒", style = MaterialTheme.typography.headlineSmall)
            }
            Text(
                item.badge.title,
                style = MaterialTheme.typography.titleSmall,
                color = TextDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                if (item.earned) item.badge.description else item.badge.criteriaDescription,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ConceptCardTile(item: ConceptCardUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (item.unlocked) Color.White else CardSurfaceAlt),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.unlocked) 3.dp else 0.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (item.unlocked) Brush.linearGradient(listOf(SpaceBluePrimary, SpaceBlueLight)) else Brush.linearGradient(listOf(LockedGray, LockedGray)),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(if (item.unlocked) "🧠" else "🔒", style = MaterialTheme.typography.titleMedium)
            }
            Column(Modifier.padding(start = 12.dp)) {
                if (item.unlocked) {
                    Text(item.card.title, style = MaterialTheme.typography.titleMedium, color = TextDark, fontWeight = FontWeight.Bold)
                    Text(item.card.shortExplanation, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                    Text("Ejemplo: ${item.card.everydayExample}", style = MaterialTheme.typography.bodySmall, color = TextMuted, modifier = Modifier.padding(top = 2.dp))
                } else {
                    Text("Tarjeta bloqueada", style = MaterialTheme.typography.titleMedium, color = TextMuted)
                }
            }
        }
    }
}
