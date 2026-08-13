package com.kidslab.physicsquest.domain.model

/**
 * Resultado de evaluar un intento de puzzle, independientemente de su
 * tipo. Todos los motores de puzzle (trayectoria, palanca, plano
 * inclinado, energía, sonido) devuelven este mismo modelo para que la
 * lógica de estrellas y progreso sea única y compartida.
 */
data class PuzzleResult(
    val success: Boolean,
    /** Qué tan cerca/óptima fue la solución, de 0f (muy lejos) a 1f (perfecta). */
    val efficiencyScore: Float,
    /** Mensaje corto y amigable para mostrar al niño/a, en español. */
    val feedbackMessage: String
)
