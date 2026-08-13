package com.kidslab.physicsquest.data.local.seed

import com.kidslab.physicsquest.data.local.entity.Hint
import com.kidslab.physicsquest.data.local.entity.Level
import com.kidslab.physicsquest.data.local.entity.LevelObject
import com.kidslab.physicsquest.data.local.entity.LevelRule
import com.kidslab.physicsquest.domain.model.LevelObjectType
import com.kidslab.physicsquest.domain.model.LevelRuleType

/**
 * Mundo 1: Movimiento — puzzle de Trayectoria.
 * Los valores de meta y obstáculos fueron verificados fuera de la app
 * (simulación equivalente a TrajectoryEngine) para asegurar que todos
 * los niveles tienen solución dentro del rango de ángulo 0-180° y
 * fuerza 0-100%.
 */
internal object SeedLevelsMovimiento {
    private const val WORLD = SeedWorlds.MOVIMIENTO
    private val LAUNCH = 0.08f to 0.85f

    private data class Cfg(
        val n: Int, val title: String, val instructions: String,
        val target: Pair<Float, Float>, val tolerance: Float,
        val obstacles: List<Pair<Float, Float>>, val maxAttemptsForStar: Int,
        val hints: List<String>
    )

    private val configs = listOf(
        Cfg(
            1, "El primer despegue",
            "Elige la dirección y la fuerza para que la sonda llegue a la primera meta.",
            0.45f to 0.78f, 0.09f, emptyList(), 3,
            listOf(
                "Prueba un ángulo cercano a 45°: no muy plano, no muy vertical.",
                "Con una fuerza media (alrededor del 80-90%) debería alcanzar la meta."
            )
        ),
        Cfg(
            2, "Más lejos",
            "La meta está más lejos. Ajusta la fuerza para que la sonda llegue más lejos.",
            0.62f to 0.65f, 0.08f, emptyList(), 3,
            listOf(
                "Un ángulo un poco más alto que en el nivel anterior ayuda a ganar altura.",
                "Prueba con una fuerza entre 60% y 80%."
            )
        ),
        Cfg(
            3, "Esquiva la roca",
            "Hay una roca en el camino. Encuentra una trayectoria que la esquive y llegue a la meta.",
            0.78f to 0.55f, 0.075f, listOf(0.45f to 0.62f), 3,
            listOf(
                "Si tu tiro choca con la roca, prueba un ángulo más alto para pasar por encima.",
                "Una fuerza cercana al 80% con un ángulo alto puede saltar la roca."
            )
        ),
        Cfg(
            4, "Dos obstáculos",
            "Ahora hay dos rocas. Encuentra el hueco entre ellas.",
            0.85f to 0.42f, 0.07f, listOf(0.4f to 0.58f, 0.62f to 0.45f), 3,
            listOf(
                "Fíjate en el espacio libre entre las dos rocas: la trayectoria debe pasar por ahí.",
                "Prueba ángulos altos (70°-80°) con fuerza fuerte (90%-100%)."
            )
        ),
        Cfg(
            5, "Precisión total",
            "La meta es pequeña y hay obstáculos. ¡Ajusta bien!",
            0.88f to 0.28f, 0.065f, listOf(0.38f to 0.5f, 0.58f to 0.35f), 2,
            listOf(
                "Necesitarás casi la máxima fuerza para llegar tan lejos y tan alto.",
                "Prueba con un ángulo entre 65° y 70°."
            )
        ),
        Cfg(
            6, "Desafío: Dra. Inercia",
            "La Dra. Inercia puso tres rocas en el camino. Combina bien dirección y fuerza para superarla.",
            0.9f to 0.22f, 0.06f, listOf(0.32f to 0.55f, 0.5f to 0.4f, 0.7f to 0.28f), 2,
            listOf(
                "El hueco más despejado está entre la segunda y la tercera roca.",
                "Usa la fuerza máxima con un ángulo alrededor de 74°."
            )
        )
    )

    val bundles: List<LevelBundle> = configs.map { c ->
        val levelId = WORLD * 100 + c.n
        val objects = mutableListOf(
            LevelObject(levelId = levelId, objectType = LevelObjectType.PELOTA, positionX = LAUNCH.first, positionY = LAUNCH.second),
            LevelObject(levelId = levelId, objectType = LevelObjectType.META, positionX = c.target.first, positionY = c.target.second, extraValue = c.tolerance)
        )
        c.obstacles.forEach { (ox, oy) ->
            objects += LevelObject(levelId = levelId, objectType = LevelObjectType.OBSTACULO, positionX = ox, positionY = oy, extraValue = 0.04f)
        }
        LevelBundle(
            level = Level(
                id = levelId, worldId = WORLD, levelNumberInWorld = c.n, title = c.title,
                instructions = c.instructions, difficulty = ((c.n - 1) / 2) + 1,
                conceptCardId = WORLD * 100 + minOf(c.n, 5)
            ),
            objects = objects,
            rules = listOf(
                LevelRule(levelId = levelId, ruleType = LevelRuleType.TOLERANCIA_META, value1 = c.tolerance),
                LevelRule(levelId = levelId, ruleType = LevelRuleType.INTENTOS_MAXIMOS_PARA_ESTRELLA, value1 = c.maxAttemptsForStar.toFloat())
            ),
            hints = c.hints.mapIndexed { idx, text -> Hint(levelId = levelId, order = idx + 1, text = text) }
        )
    }
}
