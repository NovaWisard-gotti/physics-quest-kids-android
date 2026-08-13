package com.kidslab.physicsquest.ui.puzzle.energy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.Hint
import com.kidslab.physicsquest.domain.engine.EnergyEngine
import com.kidslab.physicsquest.domain.engine.HintPolicy
import com.kidslab.physicsquest.domain.model.EnergyInput
import com.kidslab.physicsquest.domain.model.EnergyTrackSegment
import com.kidslab.physicsquest.domain.model.LevelObjectType
import com.kidslab.physicsquest.domain.model.LevelRuleType
import com.kidslab.physicsquest.domain.model.PuzzleResult
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Una pieza de pista disponible en la caja de piezas del nivel. */
data class TrackPieceUi(val id: Long, val height: Float)

data class EnergyUiState(
    val loading: Boolean = true,
    val title: String = "",
    val instructions: String = "",
    val config: EnergyEngine.TargetConfig? = null,
    val availablePieces: List<TrackPieceUi> = emptyList(),
    val chosenOrder: List<TrackPieceUi> = emptyList(),
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

class EnergyViewModel(
    private val repository: PhysicsQuestRepository,
    private val userProfileId: Long,
    private val levelId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(EnergyUiState())
    val uiState: StateFlow<EnergyUiState> = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            val level = repository.getLevel(levelId)
            val objects = repository.getObjectsForLevel(levelId)
            val rules = repository.getRulesForLevel(levelId)
            val hints = repository.getHintsForLevel(levelId)
            val start = objects.first { it.objectType == LevelObjectType.PELOTA }
            val goal = objects.first { it.objectType == LevelObjectType.META }
            val pieces = objects.filter { it.objectType == LevelObjectType.TRAMO_PISTA }
                .sortedBy { it.extraValue ?: 0f }
                .map { TrackPieceUi(it.id, it.positionY) }
            val minSpeed = rules.firstOrNull { it.ruleType == LevelRuleType.VELOCIDAD_MINIMA_LLEGADA }?.value1 ?: 0.8f
            val config = EnergyEngine.TargetConfig(startHeight = start.positionY, goalHeight = goal.positionY, minArrivalSpeed = minSpeed)
            _uiState.value = _uiState.value.copy(
                loading = false, title = level?.title.orEmpty(), instructions = level?.instructions.orEmpty(),
                config = config, availablePieces = pieces, hints = hints
            )
        }
    }

    fun addPiece(piece: TrackPieceUi) {
        val state = _uiState.value
        if (state.chosenOrder.any { it.id == piece.id }) return
        _uiState.value = state.copy(chosenOrder = state.chosenOrder + piece)
    }

    fun removeLastPiece() {
        val state = _uiState.value
        if (state.chosenOrder.isEmpty()) return
        _uiState.value = state.copy(chosenOrder = state.chosenOrder.dropLast(1))
    }

    fun clearRoute() { _uiState.value = _uiState.value.copy(chosenOrder = emptyList()) }

    fun runTrack() {
        val state = _uiState.value
        val config = state.config ?: return
        if (state.chosenOrder.isEmpty()) return
        viewModelScope.launch {
            val input = EnergyInput(state.chosenOrder.map { EnergyTrackSegment(it.height) })
            val result = EnergyEngine.evaluate(input, config)
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
            EnergyViewModel(repository, userProfileId, levelId) as T
    }
}
