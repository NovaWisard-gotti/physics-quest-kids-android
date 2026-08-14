package com.kidslab.physicsquest.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.World
import com.kidslab.physicsquest.domain.engine.WorldUnlockPolicy
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class WorldMapItem(val world: World, val starsEarned: Int, val maxStars: Int, val unlocked: Boolean)

data class MapUiState(
    val loading: Boolean = true,
    val totalStars: Int = 0,
    val worlds: List<WorldMapItem> = emptyList()
)

class MapViewModel(
    private val repository: PhysicsQuestRepository,
    private val userProfileId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            // Se combinan mundos, niveles y progreso como flujos reactivos (en vez de
            // consultas puntuales) para que las estrellas se actualicen al instante al
            // volver de un nivel, sin necesitar reiniciar la app.
            combine(
                repository.observeWorlds(),
                repository.observeAllLevels(),
                repository.observeProgressForUser(userProfileId)
            ) { fetchedWorlds, levels, progress ->
                val sortedWorlds = fetchedWorlds.sortedBy { it.order }
                val starsByLevel = progress.associate { it.levelId to it.stars }
                val starsByWorld = levels.groupBy { it.worldId }
                    .mapValues { (_, lvls) -> lvls.sumOf { starsByLevel[it.id] ?: 0 } }

                val items = mutableListOf<WorldMapItem>()
                var previousStars = Int.MAX_VALUE
                for (world in sortedWorlds) {
                    val stars = starsByWorld[world.id] ?: 0
                    val unlocked = WorldUnlockPolicy.isUnlocked(world.order, world.starsRequiredToUnlock, previousStars)
                    items += WorldMapItem(world, stars, maxStars = 18, unlocked = unlocked)
                    previousStars = stars
                }
                MapUiState(loading = false, totalStars = items.sumOf { it.starsEarned }, worlds = items)
            }.collect { _uiState.value = it }
        }
    }

    class Factory(
        private val repository: PhysicsQuestRepository,
        private val userProfileId: Long
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
            MapViewModel(repository, userProfileId) as T
    }
}
