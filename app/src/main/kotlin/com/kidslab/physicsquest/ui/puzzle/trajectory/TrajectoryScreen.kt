package com.kidslab.physicsquest.ui.puzzle.trajectory

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kidslab.physicsquest.ui.common.AnimatedStarsRow
import com.kidslab.physicsquest.ui.common.FeedbackBanner
import com.kidslab.physicsquest.ui.common.HintButton
import com.kidslab.physicsquest.ui.common.HintNote
import com.kidslab.physicsquest.ui.common.QuestPrimaryButton
import com.kidslab.physicsquest.ui.common.QuestTopBar
import com.kidslab.physicsquest.ui.theme.CoralAccent
import com.kidslab.physicsquest.ui.theme.DaySkyGradient
import com.kidslab.physicsquest.ui.theme.LeafGreen
import com.kidslab.physicsquest.ui.theme.SkyLight
import com.kidslab.physicsquest.ui.theme.SpaceBluePrimary
import com.kidslab.physicsquest.ui.theme.TextMuted

@Composable
fun TrajectoryScreen(
    viewModel: TrajectoryViewModel,
    onLevelComplete: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DaySkyGradient)) {
        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Box
        }

        // La pelota vuela de verdad por el camino previsto cuando se presiona
        // "Lanzar", en vez de aparecer directo en el resultado: se guarda una
        // foto del camino en el momento del lanzamiento (displayPath) y se
        // anima una fracción (ballT) de 0 a 1 a lo largo de ese camino.
        var displayPath by remember { mutableStateOf(state.previewPath) }
        var ballT by remember { mutableStateOf(0f) }
        var isFlying by remember { mutableStateOf(false) }

        LaunchedEffect(state.previewPath) {
            if (!isFlying) {
                displayPath = state.previewPath
                ballT = 0f
            }
        }

        LaunchedEffect(state.attemptsUsed) {
            if (state.attemptsUsed == 0) return@LaunchedEffect
            isFlying = true
            displayPath = state.previewPath
            animate(0f, 1f, animationSpec = tween(900, easing = FastOutSlowInEasing)) { value, _ -> ballT = value }
            isFlying = false
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            QuestTopBar(title = state.title, onBack = onBack, accentColor = SpaceBluePrimary)
            Text(state.instructions, style = MaterialTheme.typography.bodyLarge, color = TextMuted)

            Card(
                modifier = Modifier.fillMaxWidth().aspectRatio(1.3f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SkyLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                val config = state.config
                BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    // ballT vale 0 en reposo (antes de lanzar) y se anima hasta 1
                    // durante el vuelo; al aterrizar se queda en 1 (la pelota se
                    // ve donde cayó) hasta que el niño vuelva a mover un control.
                    val ballPoint = pointAlong(displayPath, ballT)
                    val visibleCount = if (isFlying) {
                        (1 + ballT * (displayPath.size - 1)).toInt().coerceIn(1, displayPath.size.coerceAtLeast(1))
                    } else {
                        displayPath.size
                    }

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (config == null) return@Canvas
                        fun toOffset(nx: Float, ny: Float) = Offset(nx * size.width, ny * size.height)

                        val pathPoints = displayPath.take(visibleCount).map { toOffset(it.first, it.second) }
                        for (i in 0 until pathPoints.size - 1) {
                            drawLine(SpaceBluePrimary, pathPoints[i], pathPoints[i + 1], strokeWidth = 6f)
                        }

                        drawCircle(LeafGreen, radius = config.toleranceRadius * size.minDimension, center = toOffset(config.targetX, config.targetY), style = Stroke(width = 5f))
                        drawCircle(LeafGreen.copy(alpha = 0.2f), radius = config.toleranceRadius * size.minDimension, center = toOffset(config.targetX, config.targetY))
                    }

                    // Se superponen emojis en vez de puntos de color plano: la
                    // pelota, las rocas y la meta representan lo mismo que
                    // describe el enunciado, en lugar de ser figuras abstractas.
                    if (config != null) {
                        config.obstacles.forEach { (ox, oy) ->
                            EmojiMarker("🪨", fontSize = 22.sp, nx = ox, ny = oy)
                        }
                        EmojiMarker("🚩", fontSize = 24.sp, nx = config.targetX, ny = config.targetY)
                        EmojiMarker("⚽", fontSize = 26.sp, nx = ballPoint.first, ny = ballPoint.second)
                    }
                }
            }

            Text("🎯 Ángulo: ${state.angleDegrees.toInt()}°", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Slider(
                value = state.angleDegrees,
                onValueChange = viewModel::onAngleChange,
                valueRange = 5f..175f,
                enabled = !isFlying,
                colors = SliderDefaults.colors(thumbColor = SpaceBluePrimary, activeTrackColor = SpaceBluePrimary)
            )

            Text("💥 Fuerza: ${(state.force * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Slider(
                value = state.force,
                onValueChange = viewModel::onForceChange,
                valueRange = 0.1f..1f,
                enabled = !isFlying,
                colors = SliderDefaults.colors(thumbColor = CoralAccent, activeTrackColor = CoralAccent)
            )

            if (!isFlying) {
                state.lastResult?.let { result ->
                    FeedbackBanner(result.feedbackMessage, result.success, modifier = Modifier.fillMaxWidth())
                    if (result.success) AnimatedStarsRow(state.starsEarned)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (state.levelComplete && !isFlying) {
                    QuestPrimaryButton(text = "Continuar", icon = "➡️", onClick = onLevelComplete, modifier = Modifier.fillMaxWidth())
                } else {
                    QuestPrimaryButton(text = "Lanzar", icon = "🚀", onClick = viewModel::launchBall, enabled = !isFlying, modifier = Modifier.fillMaxWidth())
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

/** Interpola la posición normalizada (0f..1f, 0f..1f) a lo largo de una lista de puntos según una fracción 0f..1f. */
private fun pointAlong(path: List<Pair<Float, Float>>, t: Float): Pair<Float, Float> {
    if (path.isEmpty()) return 0f to 0f
    if (path.size == 1) return path[0]
    val scaled = t.coerceIn(0f, 1f) * (path.size - 1)
    val i = scaled.toInt().coerceIn(0, path.size - 2)
    val frac = scaled - i
    val (x1, y1) = path[i]
    val (x2, y2) = path[i + 1]
    return (x1 + (x2 - x1) * frac) to (y1 + (y2 - y1) * frac)
}

/** Posiciona un emoji sobre el Canvas usando coordenadas normalizadas (0f..1f), igual que el dibujo. */
@Composable
private fun BoxWithConstraintsScope.EmojiMarker(
    emoji: String,
    fontSize: TextUnit,
    nx: Float,
    ny: Float
) {
    val halfSize = (fontSize.value / 2).dp
    Text(
        emoji,
        fontSize = fontSize,
        modifier = Modifier.offset(x = maxWidth * nx - halfSize, y = maxHeight * ny - halfSize)
    )
}
