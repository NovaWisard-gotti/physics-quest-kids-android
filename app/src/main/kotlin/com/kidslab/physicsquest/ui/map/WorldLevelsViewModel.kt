package com.kidslab.physicsquest.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.Level
import com.kidslab.physicsquest.data.local.entity.World
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class LevelListItem(val level: Level, val starsEarned: Int, val isBoss: Boolean)

data class WorldLevelsUiState(
    val loading: Boolean = true,
    val world: World? = null,
    val levels: List<LevelListItem> = emptyList()
)

class WorldLevelsViewModel(
    private val repository: PhysicsQuestRepository,
    private val userProfileId: Long,
    private val worldId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorldLevelsUiState())
    val uiState: StateFlow<WorldLevelsUiState> = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            val world = repository.getWorld(worldId)
            // Igual que en MapViewModel: se combina con el progreso reactivo del
            // usuario para que las estrellas se reflejen sin reiniciar la app.
            combine(
                repository.observeLevelsForWorld(worldId),
                repository.observeProgressForUser(userProfileId)
            ) { levels, progress ->
                val starsByLevel = progress.associate { it.levelId to it.stars }
                val items = levels.sortedBy { it.levelNumberInWorld }.map { level ->
                    LevelListItem(level = level, starsEarned = starsByLevel[level.id] ?: 0, isBoss = level.levelNumberInWorld == 6)
                }
                WorldLevelsUiState(loading = false, world = world, levels = items)
            }.collect { _uiState.value = it }
        }
    }

    class Factory(
        private val repository: PhysicsQuestRepository,
        private val userProfileId: Long,
        private val worldId: Long
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
            WorldLevelsViewModel(repository, userProfileId, worldId) as T
    }
}
