package com.kidslab.physicsquest.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.Badge
import com.kidslab.physicsquest.data.local.entity.ConceptCard
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ConceptCardUi(val card: ConceptCard, val unlocked: Boolean)
data class BadgeUi(val badge: Badge, val earned: Boolean)

data class InventoryUiState(
    val loading: Boolean = true,
    val conceptCards: List<ConceptCardUi> = emptyList(),
    val badges: List<BadgeUi> = emptyList()
)

class InventoryViewModel(
    private val repository: PhysicsQuestRepository,
    private val userProfileId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val progress = repository.observeProgressForUser(userProfileId)
            val cards = repository.observeConceptCards()
            val badges = repository.observeBadges()
            val userBadges = repository.observeUserBadges(userProfileId)

            kotlinx.coroutines.flow.combine(progress, cards, badges, userBadges) { p, c, b, ub ->
                val completedLevelIds = p.filter { it.completed }.map { it.levelId }.toSet()
                val unlockedConceptCardIds = mutableSetOf<Long>()
                for (levelId in completedLevelIds) {
                    repository.getLevel(levelId)?.conceptCardId?.let { unlockedConceptCardIds += it }
                }
                val earnedBadgeIds = ub.map { it.badgeId }.toSet()
                InventoryUiState(
                    loading = false,
                    conceptCards = c.map { ConceptCardUi(it, unlocked = it.id in unlockedConceptCardIds) },
                    badges = b.map { BadgeUi(it, earned = it.id in earnedBadgeIds) }
                )
            }.collect { _uiState.value = it }
        }
    }

    class Factory(
        private val repository: PhysicsQuestRepository,
        private val userProfileId: Long
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
            InventoryViewModel(repository, userProfileId) as T
    }
}
