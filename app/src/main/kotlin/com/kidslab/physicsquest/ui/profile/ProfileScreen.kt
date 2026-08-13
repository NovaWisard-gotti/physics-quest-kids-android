package com.kidslab.physicsquest.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kidslab.physicsquest.ui.common.StarsRow

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onStartAdventure: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (state.loading) {
            CircularProgressIndicator()
            return@Column
        }

        Text("🚀", style = MaterialTheme.typography.headlineLarge)
        Text("Física Quest", style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
        Text(
            "Ayuda a reconstruir la nave científica resolviendo desafíos de física por cinco mundos.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        OutlinedTextField(
            value = state.editingName,
            onValueChange = viewModel::onNameChange,
            label = { Text("¿Cómo te llamas, explorador/a?") },
            modifier = Modifier.fillMaxWidth()
        )

        state.profile?.let { profile ->
            Column(modifier = Modifier.padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Estrellas totales: ${profile.totalStars}", style = MaterialTheme.typography.titleMedium)
                Text("Piezas de nave recuperadas: ${profile.shipPiecesRecovered} / 5", style = MaterialTheme.typography.titleMedium)
                StarsRow(minOf(profile.totalStars, 3), modifier = Modifier.padding(top = 8.dp))
            }
        }

        Button(
            onClick = {
                viewModel.saveName()
                onStartAdventure()
            },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) {
            Text("Comenzar la aventura")
        }
    }
}
