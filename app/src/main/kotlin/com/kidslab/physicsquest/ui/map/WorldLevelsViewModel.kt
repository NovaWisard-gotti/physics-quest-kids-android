package com.kidslab.physicsquest.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.Level
import com.kidslab.physicsquest.data.local.entity.World
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            repository.observeLevelsForWorld(worldId).collect { levels ->
                val items = levels.sortedBy { it.levelNumberInWorld }.map { level ->
                    val p = repository.getProgress(userProfileId, level.id)
                    LevelListItem(level = level, starsEarned = p?.stars ?: 0, isBoss = level.levelNumberInWorld == 6)
                }
                _uiState.value = WorldLevelsUiState(loading = false, world = world, levels = items)
            }
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
