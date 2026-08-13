package com.kidslab.physicsquest.data.local.seed

import com.kidslab.physicsquest.data.local.entity.World
import com.kidslab.physicsquest.domain.model.PuzzleType

/** Los cinco mundos de Física Quest, en el orden en que se recorren. */
internal object SeedWorlds {
    const val MOVIMIENTO = 1L
    const val FUERZAS = 2L
    const val MAQUINAS_SIMPLES = 3L
    const val ENERGIA = 4L
    const val SONIDO_Y_ONDAS = 5L

    val all = listOf(
        World(
            id = MOVIMIENTO,
            order = 1,
            title = "Movimiento",
            subtitle = "El bosque de las trayectorias",
            description = "Lanza la pelota-sonda con la dirección y la fuerza justas para que llegue a cada meta.",
            puzzleType = PuzzleType.TRAYECTORIA,
            starsRequiredToUnlock = 0,
            shipPieceName = "Propulsor principal"
        ),
        World(
            id = FUERZAS,
            order = 2,
            title = "Fuerzas",
            subtitle = "El taller de las palancas",
            description = "Mueve el punto de apoyo de la palanca para levantar cargas cada vez más pesadas.",
            puzzleType = PuzzleType.PALANCA,
            starsRequiredToUnlock = 12,
            shipPieceName = "Brazo robótico"
        ),
        World(
            id = MAQUINAS_SIMPLES,
            order = 3,
            title = "Máquinas simples",
            subtitle = "La cantera de las rampas",
            description = "Elige la rampa adecuada para subir cada carga usando el menor esfuerzo posible.",
            puzzleType = PuzzleType.PLANO_INCLINADO,
            starsRequiredToUnlock = 12,
            shipPieceName = "Tren de aterrizaje"
        ),
        World(
            id = ENERGIA,
            order = 4,
            title = "Energía",
            subtitle = "El circuito de la montaña rusa",
            description = "Construye un recorrido que aproveche la altura para llegar a la meta con la velocidad justa.",
            puzzleType = PuzzleType.ENERGIA,
            starsRequiredToUnlock = 12,
            shipPieceName = "Reactor de energía"
        ),
        World(
            id = SONIDO_Y_ONDAS,
            order = 5,
            title = "Sonido y ondas",
            subtitle = "La torre de las ondas",
            description = "Ajusta la frecuencia y la amplitud del sonido para abrir cada puerta sónica.",
            puzzleType = PuzzleType.SONIDO,
            starsRequiredToUnlock = 12,
            shipPieceName = "Antena de comunicaciones"
        )
    )
}
