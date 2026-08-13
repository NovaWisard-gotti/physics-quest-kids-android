package com.kidslab.physicsquest.data.local.seed

import com.kidslab.physicsquest.data.local.entity.Hint
import com.kidslab.physicsquest.data.local.entity.Level
import com.kidslab.physicsquest.data.local.entity.LevelObject
import com.kidslab.physicsquest.data.local.entity.LevelRule
import com.kidslab.physicsquest.domain.model.LevelObjectType
import com.kidslab.physicsquest.domain.model.LevelRuleType

/**
 * Mundo 4: Energía — puzzle de Energía.
 * El jugador arma un recorrido (montaña rusa) escogiendo el orden de los
 * tramos de pista disponibles, para que el objeto llegue a la meta con
 * la velocidad mínima requerida. Los valores fueron verificados con una
 * simulación equivalente a EnergyEngine para asegurar que la solución
 * "usar todos los tramos en el orden sugerido" siempre funciona.
 */
internal object SeedLevelsEnergia {
    private const val WORLD = SeedWorlds.ENERGIA

    private data class Cfg(
        val n: Int, val title: String, val instructions: String,
        val startHeight: Float, val segments: List<Float>, val goalHeight: Float,
        val minSpeed: Float, val maxAttemptsForStar: Int, val hints: List<String>
    )

    private val configs = listOf(
        Cfg(1, "La primera bajada", "Arma un recorrido para que la cápsula llegue a la meta con buena velocidad.",
            1.0f, listOf(0.6f, 0.3f), 0.05f, 0.9f, 3,
            listOf("Empieza alto y deja que la altura se convierta en velocidad.", "Usa los dos tramos en el orden en que aparecen.")),
        Cfg(2, "Una subida en el camino", "Este recorrido tiene una pequeña subida antes de bajar de nuevo.",
            1.0f, listOf(0.7f, 0.5f, 0.2f), 0.05f, 1.0f, 3,
            listOf("Antes de cada subida necesitas suficiente velocidad acumulada.", "No bajes demasiado rápido al principio.")),
        Cfg(3, "Montaña rusa doble", "Dos subidas y dos bajadas: ¡organiza bien el recorrido!",
            1.0f, listOf(0.8f, 0.6f, 0.3f, 0.1f), 0.05f, 1.0f, 3,
            listOf("Cada nueva subida debe ser más baja que la anterior para no quedarte sin energía.", "Guarda algo de altura para el tramo final.")),
        Cfg(4, "El túnel profundo", "El punto de partida es un poco más bajo esta vez.",
            0.9f, listOf(0.7f, 0.4f, 0.15f), 0.02f, 1.05f, 3,
            listOf("Con menos altura inicial, cada tramo cuenta todavía más.", "Evita subidas demasiado bruscas cerca del final.")),
        Cfg(5, "La cadena de colinas", "Cuatro tramos para encadenar antes de llegar a la meta.",
            0.9f, listOf(0.75f, 0.55f, 0.3f, 0.1f), 0.02f, 1.1f, 2,
            listOf("Ordénalos de mayor a menor altura para no perder energía de más.", "El último tramo antes de la meta debe ser bajo.")),
        Cfg(6, "Desafío: Doctor Kinético", "El Doctor Kinético diseñó el recorrido más largo del reino de la energía.",
            0.85f, listOf(0.7f, 0.6f, 0.35f, 0.15f, 0.05f), 0.0f, 1.1f, 2,
            listOf("Este recorrido tiene cinco tramos: ve reduciendo la altura poco a poco.", "Llegar suave pero con velocidad: ¡ese es el equilibrio!"))
    )

    val bundles: List<LevelBundle> = configs.map { c ->
        val levelId = WORLD * 100 + c.n
        val objects = mutableListOf(
            LevelObject(levelId = levelId, objectType = LevelObjectType.PELOTA, positionX = 0.05f, positionY = c.startHeight),
            LevelObject(levelId = levelId, objectType = LevelObjectType.META, positionX = 0.95f, positionY = c.goalHeight)
        )
        c.segments.forEachIndexed { idx, h ->
            objects += LevelObject(
                levelId = levelId, objectType = LevelObjectType.TRAMO_PISTA,
                positionX = (idx + 1).toFloat() / (c.segments.size + 1), positionY = h, extraValue = (idx + 1).toFloat()
            )
        }
        LevelBundle(
            level = Level(
                id = levelId, worldId = WORLD, levelNumberInWorld = c.n, title = c.title,
                instructions = c.instructions, difficulty = ((c.n - 1) / 2) + 1,
                conceptCardId = WORLD * 100 + minOf(c.n, 5)
            ),
            objects = objects,
            rules = listOf(
                LevelRule(levelId = levelId, ruleType = LevelRuleType.VELOCIDAD_MINIMA_LLEGADA, value1 = c.minSpeed),
                LevelRule(levelId = levelId, ruleType = LevelRuleType.INTENTOS_MAXIMOS_PARA_ESTRELLA, value1 = c.maxAttemptsForStar.toFloat())
            ),
            hints = c.hints.mapIndexed { idx, text -> Hint(levelId = levelId, order = idx + 1, text = text) }
        )
    }
}
