package com.kidslab.physicsquest.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.World
import com.kidslab.physicsquest.domain.engine.WorldUnlockPolicy
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            val worlds = mutableListOf<World>()
            repository.observeWorlds().collect { fetched ->
                worlds.clear()
                worlds.addAll(fetched.sortedBy { it.order })
                val items = mutableListOf<WorldMapItem>()
                var previousStars = Int.MAX_VALUE
                for (world in worlds) {
                    val stars = repository.sumStarsForWorld(userProfileId, world.id)
                    val unlocked = WorldUnlockPolicy.isUnlocked(world.order, world.starsRequiredToUnlock, previousStars)
                    items += WorldMapItem(world, stars, maxStars = 18, unlocked = unlocked)
                    previousStars = stars
                }
                val total = repository.sumStarsForWorld(userProfileId, worlds.getOrNull(0)?.id ?: -1L).let { _ ->
                    items.sumOf { it.starsEarned }
                }
                _uiState.value = MapUiState(loading = false, totalStars = total, worlds = items)
            }
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
