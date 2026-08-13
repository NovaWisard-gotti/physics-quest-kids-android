package com.kidslab.physicsquest.ui.puzzle.lever

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.Hint
import com.kidslab.physicsquest.domain.engine.HintPolicy
import com.kidslab.physicsquest.domain.engine.LeverEngine
import com.kidslab.physicsquest.domain.model.LeverInput
import com.kidslab.physicsquest.domain.model.LevelObjectType
import com.kidslab.physicsquest.domain.model.LevelRuleType
import com.kidslab.physicsquest.domain.model.PuzzleResult
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LeverUiState(
    val loading: Boolean = true,
    val title: String = "",
    val instructions: String = "",
    val config: LeverEngine.TargetConfig? = null,
    val fulcrumPosition: Float = 0.5f,
    val effortForce: Float = 0.5f,
    val attemptsUsed: Int = 0,
    val failedAttempts: Int = 0,
    val lastResult: PuzzleResult? = null,
    val starsEarned: Int = 0,
    val hints: List<Hint> = emptyList(),
    val revealedHints: List<String> = emptyList(),
    val levelComplete: Boolean = false
) {
    val hintAvailable: Boolean get() = HintPolicy.isHintAvailable(failedAttempts) && revealedHints.size < hints.size
}

class LeverViewModel(
    private val repository: PhysicsQuestRepository,
    private val userProfileId: Long,
    private val levelId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeverUiState())
    val uiState: StateFlow<LeverUiState> = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            val level = repository.getLevel(levelId)
            val objects = repository.getObjectsForLevel(levelId)
            val rules = repository.getRulesForLevel(levelId)
            val hints = repository.getHintsForLevel(levelId)
            val load = objects.first { it.objectType == LevelObjectType.CARGA }
            val margin = rules.firstOrNull { it.ruleType == LevelRuleType.TOLERANCIA_EQUILIBRIO_TORQUE }?.value1 ?: 0.5f
            val config = LeverEngine.TargetConfig(loadWeight = load.extraValue ?: 0.5f, efficiencyMargin = margin)
            _uiState.value = _uiState.value.copy(
                loading = false, title = level?.title.orEmpty(), instructions = level?.instructions.orEmpty(),
                config = config, hints = hints
            )
        }
    }

    fun onFulcrumChange(value: Float) { _uiState.value = _uiState.value.copy(fulcrumPosition = value) }
    fun onEffortChange(value: Float) { _uiState.value = _uiState.value.copy(effortForce = value) }

    fun tryLift() {
        val state = _uiState.value
        val config = state.config ?: return
        viewModelScope.launch {
            val result = LeverEngine.evaluate(LeverInput(state.fulcrumPosition, state.effortForce), config)
            val attempts = state.attemptsUsed + 1
            val attempt = repository.recordAttempt(userProfileId, levelId, result, attempts, state.revealedHints.isNotEmpty())
            _uiState.value = state.copy(
                attemptsUsed = attempts,
                failedAttempts = if (result.success) state.failedAttempts else state.failedAttempts + 1,
                lastResult = result,
                starsEarned = maxOf(state.starsEarned, attempt.starsEarned),
                levelComplete = result.success
            )
        }
    }

    fun requestHint() {
        val state = _uiState.value
        if (!state.hintAvailable) return
        val nextHint = state.hints.getOrNull(state.revealedHints.size) ?: return
        _uiState.value = state.copy(revealedHints = state.revealedHints + nextHint.text)
    }

    class Factory(
        private val repository: PhysicsQuestRepository,
        private val userProfileId: Long,
        private val levelId: Long
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
            LeverViewModel(repository, userProfileId, levelId) as T
    }
}
