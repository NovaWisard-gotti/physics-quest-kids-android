package com.kidslab.physicsquest.domain.model

/** Entrada del jugador para el puzzle de Trayectoria (Mundo 1: Movimiento). */
data class TrajectoryInput(
    /** Ángulo de lanzamiento en grados, 0..180 (0 = horizontal derecha, 90 = recto hacia arriba). */
    val angleDegrees: Float,
    /** Fuerza de lanzamiento, 0f..1f (0% a 100%). */
    val force: Float
)

/** Entrada del jugador para el puzzle de Palanca (Mundo 2: Fuerzas). */
data class LeverInput(
    /** Posición del punto de apoyo a lo largo de la barra, 0f..1f. */
    val fulcrumPosition: Float,
    /** Fuerza de esfuerzo que el explorador decide aplicar, 0f..1f. */
    val effortForce: Float
)

/** Entrada del jugador para el puzzle de Plano Inclinado (Mundo 3: Máquinas simples). */
data class InclinedPlaneInput(
    /** Índice de la rampa elegida entre las disponibles en el nivel (LevelObject tipo RAMPA). */
    val chosenRampObjectId: Long
)

/** Un tramo de pista elegido por el jugador para el puzzle de Energía (Mundo 4). */
data class EnergyTrackSegment(val heightNormalized: Float)

/** Entrada del jugador para el puzzle de Energía: la secuencia de tramos que arma. */
data class EnergyInput(val segments: List<EnergyTrackSegment>)

/** Entrada del jugador para el puzzle de Sonido (Mundo 5: Sonido y ondas). */
data class SoundInput(
    /** Frecuencia elegida en Hz. Más baja = más grave, más alta = más agudo. */
    val frequencyHz: Float,
    /** Amplitud elegida, 0f..1f. Más baja = más suave, más alta = más fuerte. */
    val amplitude: Float
)
