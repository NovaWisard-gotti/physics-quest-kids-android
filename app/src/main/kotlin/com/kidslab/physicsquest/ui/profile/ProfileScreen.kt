package com.kidslab.physicsquest.ui.profile

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kidslab.physicsquest.ui.common.QuestPrimaryButton
import com.kidslab.physicsquest.ui.common.StarsRow
import com.kidslab.physicsquest.ui.theme.CoralAccent
import com.kidslab.physicsquest.ui.theme.NightSkyGradient
import com.kidslab.physicsquest.ui.theme.SpaceBluePrimary
import com.kidslab.physicsquest.ui.theme.SunshineYellow
import com.kidslab.physicsquest.ui.theme.VioletAccent

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onStartAdventure: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(NightSkyGradient)) {
        // Planetas y lunas decorativos, puramente ambientales.
        Box(Modifier.offset(x = 24.dp, y = 90.dp).size(26.dp).background(SunshineYellow.copy(alpha = 0.55f), shape = CircleShape))
        Box(Modifier.offset(x = 300.dp, y = 140.dp).size(14.dp).background(Color.White.copy(alpha = 0.5f), shape = CircleShape))
        Box(Modifier.offset(x = 300.dp, y = 560.dp).size(40.dp).background(VioletAccent.copy(alpha = 0.35f), shape = CircleShape))
        Box(Modifier.offset(x = (-20).dp, y = 500.dp).size(70.dp).background(CoralAccent.copy(alpha = 0.25f), shape = CircleShape))

        if (state.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "rocketFloat")
            val floatOffset by infiniteTransition.animateFloat(
                initialValue = -8f,
                targetValue = 8f,
                animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse),
                label = "rocketFloatValue"
            )
            Text(
                "🚀",
                fontSize = 56.sp,
                modifier = Modifier.offset(y = floatOffset.dp)
            )

            Text(
                "Física Quest",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "Ayuda a reconstruir la nave científica resolviendo desafíos de física por cinco mundos.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp, bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "¿Cómo te llamas, explorador/a?",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    // El campo arranca vacío: "Explorador/a" solo aparece como
                    // marca de agua (placeholder) hasta que el niño escriba su
                    // propio nombre, en vez de venir preescrito y seleccionado.
                    OutlinedTextField(
                        value = state.editingName,
                        onValueChange = viewModel::onNameChange,
                        placeholder = { Text(DEFAULT_EXPLORER_NAME) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SpaceBluePrimary,
                            unfocusedBorderColor = SpaceBluePrimary.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    state.profile?.let { profile ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                                .background(Color(0xFFF0F4FF), RoundedCornerShape(18.dp))
                                .padding(vertical = 14.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⭐ ${profile.totalStars}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                                Text("estrellas", style = MaterialTheme.typography.bodySmall)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🛰️ ${profile.shipPiecesRecovered}/5", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                                Text("piezas de nave", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        StarsRow(minOf(profile.totalStars, 3), modifier = Modifier.padding(top = 12.dp), starSize = 26.dp)
                    }

                    QuestPrimaryButton(
                        text = "Comenzar la aventura",
                        icon = "🚀",
                        onClick = {
                            viewModel.saveName()
                            onStartAdventure()
                        },
                        containerColor = SpaceBluePrimary,
                        modifier = Modifier.fillMaxWidth().padding(top = 22.dp)
                    )
                }
            }
        }
    }
}
