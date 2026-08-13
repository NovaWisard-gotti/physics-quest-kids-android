package com.kidslab.physicsquest.domain.engine

import com.kidslab.physicsquest.domain.model.InclinedPlaneInput
import com.kidslab.physicsquest.domain.model.PuzzleResult

/**
 * Motor del puzzle de Plano Inclinado (Mundo 3: Máquinas simples).
 *
 * El jugador elige entre varias rampas (cada una con una longitud y una
 * altura) para subir una carga hasta una plataforma. Se usa la idea
 * simplificada de que el esfuerzo necesario es menor cuanto más larga es
 * la rampa para la misma altura (esfuerzo ≈ peso * altura / longitud),
 * sin pedir ningún cálculo al jugador: solo comparar rampas.
 */
object InclinedPlaneEngine {

    data class RampOption(
        val objectId: Long,
        val length: Float,
        val height: Float
    )

    data class TargetConfig(
        val loadWeight: Float,
        val maxAvailableEffort: Float,
        val ramps: List<RampOption>
    )

    private fun effortFor(ramp: RampOption, loadWeight: Float): Float =
        if (ramp.length <= 0f) Float.MAX_VALUE else loadWeight * (ramp.height / ramp.length)

    fun evaluate(input: InclinedPlaneInput, config: TargetConfig): PuzzleResult {
        val chosen = config.ramps.firstOrNull { it.objectId == input.chosenRampObjectId }
            ?: return PuzzleResult(false, 0f, "Elige una rampa para continuar.")

        val effort = effortFor(chosen, config.loadWeight)
        val success = effort <= config.maxAvailableEffort

        if (!success) {
            return PuzzleResult(
                success = false,
                efficiencyScore = 0f,
                feedbackMessage = "Esa rampa es demasiado empinada: necesitarías mucha más fuerza. Prueba una más larga."
            )
        }

        val minEffort = config.ramps.minOf { effortFor(it, config.loadWeight) }
        val efficiency = if (effort <= 0f) 1f else (minEffort / effort).coerceIn(0f, 1f)

        val message = if (efficiency > 0.9f) {
            "¡Perfecto! Elegiste la rampa que pide menos esfuerzo para subir la carga."
        } else {
            "¡Subiste la carga! Una rampa más larga habría necesitado todavía menos esfuerzo."
        }

        return PuzzleResult(success = true, efficiencyScore = efficiency, feedbackMessage = message)
    }
}
