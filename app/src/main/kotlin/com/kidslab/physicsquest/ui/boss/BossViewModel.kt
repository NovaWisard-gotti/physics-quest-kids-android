package com.kidslab.physicsquest.ui.boss

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.BossChallenge
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BossUiState(val loading: Boolean = true, val boss: BossChallenge? = null)

class BossViewModel(
    private val repository: PhysicsQuestRepository,
    private val worldId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow(BossUiState())
    val uiState: StateFlow<BossUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val boss = repository.getBossChallengeForWorld(worldId)
            _uiState.value = BossUiState(loading = false, boss = boss)
        }
    }

    class Factory(
        private val repository: PhysicsQuestRepository,
        private val worldId: Long
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
            BossViewModel(repository, worldId) as T
    }
}
