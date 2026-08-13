package com.kidslab.physicsquest.domain.engine

import com.kidslab.physicsquest.domain.model.EnergyInput
import com.kidslab.physicsquest.domain.model.PuzzleResult
import kotlin.math.sqrt

/**
 * Motor del puzzle de Energía (Mundo 4: Energía).
 *
 * El jugador arma un recorrido (una secuencia de alturas de pista, tipo
 * montaña rusa) para llevar un objeto desde el punto de salida hasta la
 * meta. Se simula la conservación de energía de forma simplificada:
 * energía potencial (altura) se convierte en energía cinética
 * (velocidad), con una pequeña pérdida por "fricción" en cada tramo.
 *
 * Alturas normalizadas en 0f (nivel del suelo) .. 1f (más alto).
 */
object EnergyEngine {
    private const val FRICTION_LOSS_PER_SEGMENT = 0.04f

    data class TargetConfig(
        val startHeight: Float,
        val goalHeight: Float,
        /** Velocidad mínima (energía cinética) que debe tener el objeto al llegar a la meta. */
        val minArrivalSpeed: Float
    )

    fun evaluate(input: EnergyInput, config: TargetConfig): PuzzleResult {
        if (input.segments.isEmpty()) {
            return PuzzleResult(false, 0f, "Arma un recorrido con al menos un tramo de pista.")
        }

        var currentHeight = config.startHeight
        var speed = 0f
        val allHeights = input.segments.map { it.heightNormalized } + config.goalHeight

        for (nextHeight in allHeights) {
            // Energía disponible en la cima actual, en términos de "altura equivalente".
            val availableEnergyHeight = currentHeight + (speed * speed) / 2f

            if (nextHeight > availableEnergyHeight) {
                return PuzzleResult(
                    success = false,
                    efficiencyScore = 0f,
                    feedbackMessage = "El objeto se quedó sin energía para subir ese tramo. Prueba empezar más alto o suavizar la subida."
                )
            }

            val heightDrop = (availableEnergyHeight - nextHeight).coerceAtLeast(0f)
            speed = sqrt((heightDrop * 2f).coerceAtLeast(0f)) - FRICTION_LOSS_PER_SEGMENT
            speed = speed.coerceAtLeast(0f)
            currentHeight = nextHeight
        }

        val success = speed >= config.minArrivalSpeed
        if (!success) {
            return PuzzleResult(
                success = false,
                efficiencyScore = 0f,
                feedbackMessage = "El objeto llegó demasiado despacio a la meta. Prueba un recorrido con más bajada al final."
            )
        }

        val efficiency = (speed / (config.minArrivalSpeed * 1.8f)).coerceIn(0f, 1f)
        val message = if (efficiency > 0.85f) {
            "¡Recorrido genial! El objeto llegó a la meta con la velocidad perfecta."
        } else {
            "¡El objeto llegó a la meta! Podrías afinar el recorrido para que llegue con mejor ritmo."
        }
        return PuzzleResult(success = true, efficiencyScore = efficiency, feedbackMessage = message)
    }
}
