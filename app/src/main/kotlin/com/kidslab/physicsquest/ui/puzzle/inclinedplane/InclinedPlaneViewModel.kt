package com.kidslab.physicsquest.ui.puzzle.inclinedplane

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.Hint
import com.kidslab.physicsquest.domain.engine.HintPolicy
import com.kidslab.physicsquest.domain.engine.InclinedPlaneEngine
import com.kidslab.physicsquest.domain.model.InclinedPlaneInput
import com.kidslab.physicsquest.domain.model.LevelObjectType
import com.kidslab.physicsquest.domain.model.LevelRuleType
import com.kidslab.physicsquest.domain.model.PuzzleResult
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RampOptionUi(val objectId: Long, val label: String, val length: Float, val height: Float)

data class InclinedPlaneUiState(
    val loading: Boolean = true,
    val title: String = "",
    val instructions: String = "",
    val config: InclinedPlaneEngine.TargetConfig? = null,
    val ramps: List<RampOptionUi> = emptyList(),
    val selectedRampId: Long? = null,
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

class InclinedPlaneViewModel(
    private val repository: PhysicsQuestRepository,
    private val userProfileId: Long,
    private val levelId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(InclinedPlaneUiState())
    val uiState: StateFlow<InclinedPlaneUiState> = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            val level = repository.getLevel(levelId)
            val objects = repository.getObjectsForLevel(levelId)
            val rules = repository.getRulesForLevel(levelId)
            val hints = repository.getHintsForLevel(levelId)
            val load = objects.first { it.objectType == LevelObjectType.CARGA }
            val rampObjects = objects.filter { it.objectType == LevelObjectType.RAMPA }
            val maxEffort = rules.firstOrNull { it.ruleType == LevelRuleType.ESFUERZO_MAXIMO_DISPONIBLE }?.value1 ?: 0.6f

            // El orden en pantalla se mezcla (no siempre corta/media/larga de
            // arriba a abajo) para que la posición no delate la respuesta:
            // así el niño tiene que fijarse en la longitud de cada rampa en
            // vez de aprender "la de más abajo siempre es la correcta".
            val ramps = rampObjects.shuffled().map {
                RampOptionUi(objectId = it.id, label = it.extraLabel ?: "Rampa", length = it.extraValue ?: it.positionX, height = it.positionY)
            }
            val config = InclinedPlaneEngine.TargetConfig(
                loadWeight = load.extraValue ?: 0.5f,
                maxAvailableEffort = maxEffort,
                ramps = rampObjects.map { InclinedPlaneEngine.RampOption(it.id, it.extraValue ?: it.positionX, it.positionY) }
            )
            _uiState.value = _uiState.value.copy(
                loading = false, title = level?.title.orEmpty(), instructions = level?.instructions.orEmpty(),
                config = config, ramps = ramps, hints = hints
            )
        }
    }

    fun onSelectRamp(objectId: Long) { _uiState.value = _uiState.value.copy(selectedRampId = objectId) }

    fun tryClimb() {
        val state = _uiState.value
        val config = state.config ?: return
        val selected = state.selectedRampId ?: return
        viewModelScope.launch {
            val result = InclinedPlaneEngine.evaluate(InclinedPlaneInput(selected), config)
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
            InclinedPlaneViewModel(repository, userProfileId, levelId) as T
    }
}
