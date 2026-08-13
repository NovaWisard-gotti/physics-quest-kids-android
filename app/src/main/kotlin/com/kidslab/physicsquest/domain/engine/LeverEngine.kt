package com.kidslab.physicsquest.domain.engine

import com.kidslab.physicsquest.domain.model.LeverInput
import com.kidslab.physicsquest.domain.model.PuzzleResult
import kotlin.math.abs

/**
 * Motor del puzzle de Palanca (Mundo 2: Fuerzas).
 *
 * El jugador mueve el punto de apoyo a lo largo de una barra y elige
 * cuánta fuerza aplicar, para levantar una carga situada en el extremo
 * opuesto. Internamente se usa la ley de la palanca (equilibrio de
 * torques): fuerza_esfuerzo * brazo_esfuerzo = peso_carga * brazo_carga.
 *
 * La barra va de 0f (extremo izquierdo, donde está la carga) a 1f
 * (extremo derecho, donde el explorador empuja).
 */
object LeverEngine {

    data class TargetConfig(
        val loadWeight: Float,
        val loadPosition: Float = 0f,
        val effortPosition: Float = 1f,
        /** Margen de tolerancia: cuánta fuerza "de sobra" se permite sin considerarlo un fallo. */
        val efficiencyMargin: Float = 0.6f
    )

    fun evaluate(input: LeverInput, config: TargetConfig): PuzzleResult {
        val rawLoadArm = abs(input.fulcrumPosition - config.loadPosition)
        val rawEffortArm = abs(config.effortPosition - input.fulcrumPosition)

        if (rawLoadArm <= 0.01f || rawEffortArm <= 0.01f) {
            return PuzzleResult(
                success = false,
                efficiencyScore = 0f,
                feedbackMessage = "El punto de apoyo está demasiado cerca de un extremo. Muévelo hacia el centro."
            )
        }

        val fulcrum = input.fulcrumPosition.coerceIn(0.01f, 0.99f)
        val loadArm = abs(fulcrum - config.loadPosition)
        val effortArm = abs(config.effortPosition - fulcrum)

        val requiredEffort = (config.loadWeight * loadArm) / effortArm
        val success = input.effortForce >= requiredEffort && input.effortForce <= 1f

        if (!success) {
            val message = if (input.effortForce < requiredEffort) {
                "La palanca no se mueve: necesitas más fuerza o acercar el punto de apoyo a la carga."
            } else {
                "Estás empujando con más fuerza de la que tienes disponible."
            }
            return PuzzleResult(success = false, efficiencyScore = 0f, feedbackMessage = message)
        }

        // Eficiencia: qué tan cerca estuvo del esfuerzo mínimo necesario (idea de ventaja mecánica).
        val excess = (input.effortForce - requiredEffort).coerceAtLeast(0f)
        val efficiency = (1f - (excess / config.efficiencyMargin).coerceIn(0f, 1f)).coerceIn(0f, 1f)

        val message = if (efficiency > 0.85f) {
            "¡Excelente! Encontraste el punto de apoyo perfecto: levantaste la carga con muy poco esfuerzo."
        } else {
            "¡Lograste levantar la carga! Si acercas el punto de apoyo a la carga, necesitarás aún menos fuerza."
        }

        return PuzzleResult(success = true, efficiencyScore = efficiency, feedbackMessage = message)
    }
}
