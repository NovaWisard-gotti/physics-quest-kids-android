package com.kidslab.physicsquest.domain.model

/**
 * Tipo de puzzle asociado a cada nivel. Cada mundo de Física Quest usa
 * exactamente un tipo de puzzle, lo que permite añadir niveles nuevos
 * sin tener que escribir pantallas nuevas: basta con agregar datos.
 */
enum class PuzzleType {
    TRAYECTORIA,   // Mundo 1: Movimiento
    PALANCA,       // Mundo 2: Fuerzas
    PLANO_INCLINADO, // Mundo 3: Máquinas simples
    ENERGIA,       // Mundo 4: Energía
    SONIDO         // Mundo 5: Sonido y ondas
}

/** Tipo de objeto que puede aparecer dentro de un nivel (LevelObject). */
enum class LevelObjectType {
    PELOTA,
    META,
    OBSTACULO,
    APOYO_PALANCA,
    CARGA,
    RAMPA,
    PARED,
    ALTAVOZ,
    TRAMO_PISTA
}

/** Tipo de regla de victoria/puntuación asociada a un nivel (LevelRule). */
enum class LevelRuleType {
    TOLERANCIA_META,
    INTENTOS_MAXIMOS_PARA_ESTRELLA,
    UMBRAL_EFICIENCIA,
    ALTURA_MINIMA,
    ALTURA_MAXIMA,
    RANGO_FRECUENCIA,
    RANGO_AMPLITUD,
    TOLERANCIA_EQUILIBRIO_TORQUE,
    ESFUERZO_MAXIMO_DISPONIBLE,
    VELOCIDAD_MINIMA_LLEGADA
}

/** Mundos de la aventura, en orden de desbloqueo. */
enum class WorldTheme(val order: Int) {
    MOVIMIENTO(1),
    FUERZAS(2),
    MAQUINAS_SIMPLES(3),
    ENERGIA(4),
    SONIDO_Y_ONDAS(5)
}
