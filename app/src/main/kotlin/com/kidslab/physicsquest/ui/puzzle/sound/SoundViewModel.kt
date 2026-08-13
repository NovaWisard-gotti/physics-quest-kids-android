package com.kidslab.physicsquest.ui.puzzle.sound

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.Hint
import com.kidslab.physicsquest.domain.engine.HintPolicy
import com.kidslab.physicsquest.domain.engine.SoundEngine
import com.kidslab.physicsquest.domain.model.LevelRuleType
import com.kidslab.physicsquest.domain.model.PuzzleResult
import com.kidslab.physicsquest.domain.model.SoundInput
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SoundUiState(
    val loading: Boolean = true,
    val title: String = "",
    val instructions: String = "",
    val config: SoundEngine.TargetConfig? = null,
    val frequencyHz: Float = 500f,
    val amplitude: Float = 0.5f,
    val attemptsUsed: Int = 0,
    val failedAttempts: Int = 0,
    val lastResult: PuzzleResult? = null,
    val starsEarned: Int = 0,
    val hints: List<Hint> = emptyList(),
    val revealedHints: List<String> = emptyList(),
    val levelComplete: Boolean = false
) {
    val hintAvailable: Boolean get() = HintPolicy.isHintAvailable(failedAttempts) && revealedHints.size < hints.size
    val pitchLabel: String get() = SoundEngine.pitchLabel(frequencyHz)
    val loudnessLabel: String get() = SoundEngine.loudnessLabel(amplitude)
}

class SoundViewModel(
    private val repository: PhysicsQuestRepository,
    private val userProfileId: Long,
    private val levelId: Long,
    private val player: SoundPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(SoundUiState())
    val uiState: StateFlow<SoundUiState> = _uiState.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            val level = repository.getLevel(levelId)
            val rules = repository.getRulesForLevel(levelId)
            val hints = repository.getHintsForLevel(levelId)
            val freqRule = rules.first { it.ruleType == LevelRuleType.RANGO_FRECUENCIA }
            val ampRule = rules.first { it.ruleType == LevelRuleType.RANGO_AMPLITUD }
            val config = SoundEngine.TargetConfig(
                minFrequencyHz = freqRule.value1, maxFrequencyHz = freqRule.value2 ?: freqRule.value1,
                minAmplitude = ampRule.value1, maxAmplitude = ampRule.value2 ?: ampRule.value1
            )
            _uiState.value = _uiState.value.copy(
                loading = false, title = level?.title.orEmpty(), instructions = level?.instructions.orEmpty(),
                config = config, hints = hints
            )
        }
    }

    fun onFrequencyChange(value: Float) { _uiState.value = _uiState.value.copy(frequencyHz = value) }
    fun onAmplitudeChange(value: Float) { _uiState.value = _uiState.value.copy(amplitude = value) }

    fun playPreview() {
        val state = _uiState.value
        player.playTone(state.frequencyHz, state.amplitude)
    }

    fun submit() {
        val state = _uiState.value
        val config = state.config ?: return
        viewModelScope.launch {
            val result = SoundEngine.evaluate(SoundInput(state.frequencyHz, state.amplitude), config)
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

    override fun onCleared() {
        super.onCleared()
        player.stop()
    }

    class Factory(
        private val repository: PhysicsQuestRepository,
        private val userProfileId: Long,
        private val levelId: Long,
        private val player: SoundPlayer
    ) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
            SoundViewModel(repository, userProfileId, levelId, player) as T
    }
}
