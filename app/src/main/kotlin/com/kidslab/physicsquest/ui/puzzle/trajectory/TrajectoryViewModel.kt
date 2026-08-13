package com.kidslab.physicsquest.ui.puzzle.trajectory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.Hint
import com.kidslab.physicsquest.data.local.entity.LevelObject
import com.kidslab.physicsquest.domain.engine.HintPolicy
import com.kidslab.physicsquest.domain.engine.TrajectoryEngine
import com.kidslab.physicsquest.domain.model.LevelObjectType
import com.kidslab.physicsquest.domain.model.PuzzleResult
import com.kidslab.physicsquest.domain.model.TrajectoryInput
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TrajectoryUiState(
    val loading: Boolean = true,
    val title: String = "",
    val instructions: String = "",
    val config: TrajectoryEngine.TargetConfig? = null,
    val angleDegrees: Float = 45f,
    val force: Float = 0.6f,
    val previewPath: List<Pair<Float, Float>> = emptyList(),
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

class TrajectoryViewModel(
    private val repository: PhysicsQuestRepository,
    private val userProfileId: Long,
    private val levelId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrajectoryUiState())
    val uiState: StateFlow<TrajectoryUiState> = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            val level = repository.getLevel(levelId)
            val objects = repository.getObjectsForLevel(levelId)
            val hints = repository.getHintsForLevel(levelId)
            val launch = objects.first { it.objectType == LevelObjectType.PELOTA }
            val target = objects.first { it.objectType == LevelObjectType.META }
            val obstacles = objects.filter { it.objectType == LevelObjectType.OBSTACULO }
            val config = TrajectoryEngine.TargetConfig(
                launchX = launch.positionX, launchY = launch.positionY,
                targetX = target.positionX, targetY = target.positionY,
                toleranceRadius = target.extraValue ?: 0.08f,
                obstacles = obstacles.map { it.positionX to it.positionY },
                obstacleRadius = obstacles.firstOrNull()?.extraValue ?: 0.04f
            )
            _uiState.value = _uiState.value.copy(
                loading = false,
                title = level?.title.orEmpty(),
                instructions = level?.instructions.orEmpty(),
                config = config,
                hints = hints,
                previewPath = TrajectoryEngine.simulatePath(TrajectoryInput(45f, 0.6f), config)
            )
        }
    }

    fun onAngleChange(angle: Float) {
        val state = _uiState.value
        val config = state.config ?: return
        val path = TrajectoryEngine.simulatePath(TrajectoryInput(angle, state.force), config)
        _uiState.value = state.copy(angleDegrees = angle, previewPath = path)
    }

    fun onForceChange(force: Float) {
        val state = _uiState.value
        val config = state.config ?: return
        val path = TrajectoryEngine.simulatePath(TrajectoryInput(state.angleDegrees, force), config)
        _uiState.value = state.copy(force = force, previewPath = path)
    }

    fun launchBall() {
        val state = _uiState.value
        val config = state.config ?: return
        viewModelScope.launch {
            val result = TrajectoryEngine.evaluate(TrajectoryInput(state.angleDegrees, state.force), config)
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

    fun retry() {
        _uiState.value = _uiState.value.copy(lastResult = null)
    }

    class Factory(
        private val repository: PhysicsQuestRepository,
        private val userProfileId: Long,
        private val levelId: Long
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
            TrajectoryViewModel(repository, userProfileId, levelId) as T
    }
}
