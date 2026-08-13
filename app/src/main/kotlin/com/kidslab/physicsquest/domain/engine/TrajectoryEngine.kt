package com.kidslab.physicsquest.domain.engine

import com.kidslab.physicsquest.domain.model.PuzzleResult
import com.kidslab.physicsquest.domain.model.TrajectoryInput
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Motor del puzzle de Trayectoria (Mundo 1: Movimiento).
 *
 * El niño o niña elige una dirección y una fuerza para que una pelota
 * llegue a la meta. No se pide ninguna ecuación: internamente se simula
 * un tiro parabólico simplificado (gravedad constante) y se comprueba si
 * la trayectoria pasa lo bastante cerca de la meta.
 *
 * Todo el espacio de juego está normalizado en 0f..1f, con el origen
 * (0,0) en la esquina superior izquierda e Y creciendo hacia abajo, para
 * poder dibujarse directamente en un Canvas de Compose.
 */
object TrajectoryEngine {
    private const val GRAVITY = 1.6f
    private const val TIME_STEP = 0.02f
    private const val MAX_TIME = 4f

    data class TargetConfig(
        val launchX: Float,
        val launchY: Float,
        val targetX: Float,
        val targetY: Float,
        val toleranceRadius: Float,
        val obstacles: List<Pair<Float, Float>> = emptyList(),
        val obstacleRadius: Float = 0.04f
    )

    /** Calcula los puntos de la trayectoria, útiles para dibujarla en pantalla. */
    fun simulatePath(input: TrajectoryInput, config: TargetConfig): List<Pair<Float, Float>> {
        val angleRad = Math.toRadians(input.angleDegrees.toDouble())
        val speed = input.force * 1.8f
        var vx = (speed * cos(angleRad)).toFloat()
        var vy = -(speed * sin(angleRad)).toFloat() // Y crece hacia abajo, así que restamos.
        var x = config.launchX
        var y = config.launchY
        val points = mutableListOf(x to y)
        var t = 0f
        while (t < MAX_TIME && y <= 1.05f && x in -0.05f..1.05f) {
            vy += GRAVITY * TIME_STEP
            x += vx * TIME_STEP
            y += vy * TIME_STEP
            points += x to y
            t += TIME_STEP
        }
        return points
    }

    fun evaluate(input: TrajectoryInput, config: TargetConfig): PuzzleResult {
        val path = simulatePath(input, config)

        // Si la trayectoria pasa demasiado cerca de un obstáculo, falla.
        for ((ox, oy) in config.obstacles) {
            if (path.any { (px, py) -> hypot((px - ox).toDouble(), (py - oy).toDouble()) < config.obstacleRadius }) {
                return PuzzleResult(
                    success = false,
                    efficiencyScore = 0f,
                    feedbackMessage = "¡Cuidado! La pelota chocó con un obstáculo. Prueba otra dirección o fuerza."
                )
            }
        }

        val closestDistance = path.minOf { (px, py) ->
            hypot((px - config.targetX).toDouble(), (py - config.targetY).toDouble()).toFloat()
        }

        val success = closestDistance <= config.toleranceRadius
        val efficiency = (1f - (closestDistance / config.toleranceRadius).coerceIn(0f, 1f)).coerceIn(0f, 1f)

        val message = when {
            success && efficiency > 0.85f -> "¡Tiro perfecto! La pelota llegó justo al centro de la meta."
            success -> "¡Bien hecho! La pelota llegó a la meta."
            closestDistance < config.toleranceRadius * 2 -> "¡Casi! Ajusta un poco la fuerza o el ángulo."
            else -> "La pelota no llegó a la meta. Prueba con otra dirección o fuerza."
        }

        return PuzzleResult(success = success, efficiencyScore = if (success) efficiency else 0f, feedbackMessage = message)
    }
}
