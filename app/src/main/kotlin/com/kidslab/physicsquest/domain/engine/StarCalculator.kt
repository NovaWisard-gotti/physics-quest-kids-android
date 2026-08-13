package com.kidslab.physicsquest.domain.engine

import com.kidslab.physicsquest.domain.model.PuzzleResult

/**
 * Regla de estrellas de Física Quest, igual para los 30 niveles:
 *
 * 1 estrella -> completar el nivel (success = true).
 * 1 estrella -> lograrlo en pocos intentos (attemptsUsed <= maxAttemptsForStar).
 * 1 estrella -> solución eficiente (efficiencyScore >= umbral de eficiencia).
 *
 * Los fallos NUNCA penalizan de forma permanente: solo cuenta el mejor
 * resultado histórico guardado en LevelProgress.
 */
object StarCalculator {
    const val DEFAULT_EFFICIENCY_THRESHOLD = 0.75f
    const val DEFAULT_MAX_ATTEMPTS_FOR_STAR = 2

    fun calculateStars(
        result: PuzzleResult,
        attemptsUsedIncludingThisOne: Int,
        maxAttemptsForStar: Int = DEFAULT_MAX_ATTEMPTS_FOR_STAR,
        efficiencyThreshold: Float = DEFAULT_EFFICIENCY_THRESHOLD
    ): Int {
        if (!result.success) return 0
        var stars = 1 // por completar
        if (attemptsUsedIncludingThisOne <= maxAttemptsForStar) stars++
        if (result.efficiencyScore >= efficiencyThreshold) stars++
        return stars
    }
}

/**
 * Política de pistas: se habilitan a partir del segundo intento fallido
 * (es decir, antes del tercer intento) y nunca restan puntuación.
 */
object HintPolicy {
    const val FAILED_ATTEMPTS_BEFORE_HINT = 2

    fun isHintAvailable(failedAttemptsSoFar: Int): Boolean =
        failedAttemptsSoFar >= FAILED_ATTEMPTS_BEFORE_HINT
}
