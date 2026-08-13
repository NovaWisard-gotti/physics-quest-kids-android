package com.kidslab.physicsquest.data.local.seed

import com.kidslab.physicsquest.data.local.entity.Hint
import com.kidslab.physicsquest.data.local.entity.Level
import com.kidslab.physicsquest.data.local.entity.LevelObject
import com.kidslab.physicsquest.data.local.entity.LevelRule
import com.kidslab.physicsquest.domain.model.LevelObjectType
import com.kidslab.physicsquest.domain.model.LevelRuleType

/**
 * Mundo 3: Máquinas simples — puzzle de Plano Inclinado.
 * Cada nivel ofrece tres rampas (corta, media y larga) para la misma
 * altura de plataforma. La rampa corta siempre exige más esfuerzo del
 * disponible; la media y la larga sí sirven, siendo la larga la más
 * eficiente (menos esfuerzo, más distancia recorrida).
 */
internal object SeedLevelsMaquinasSimples {
    private const val WORLD = SeedWorlds.MAQUINAS_SIMPLES

    private data class Cfg(
        val n: Int, val title: String, val instructions: String,
        val loadWeight: Float, val height: Float, val lengths: List<Float>,
        val maxEffort: Float, val maxAttemptsForStar: Int, val hints: List<String>
    )

    private val configs = listOf(
        Cfg(1, "La primera rampa", "Elige la rampa que permita subir la caja sin demasiado esfuerzo.",
            0.5f, 1.0f, listOf(0.9f, 1.6f, 2.4f), 0.45f, 3,
            listOf("La rampa más corta es demasiado empinada.", "Compara: a más longitud, menos esfuerzo para la misma altura.")),
        Cfg(2, "Subiendo el barril", "El barril es un poco más pesado. ¿Qué rampa eliges?",
            0.6f, 1.0f, listOf(1.0f, 1.8f, 2.6f), 0.45f, 3,
            listOf("Descarta la rampa corta: es demasiado esfuerzo.", "La rampa larga siempre pide menos esfuerzo.")),
        Cfg(3, "La plataforma alta", "La plataforma está más alta. Elige con cuidado.",
            0.7f, 1.2f, listOf(1.1f, 2.0f, 3.0f), 0.55f, 3,
            listOf("A mayor altura, se nota aún más la diferencia entre rampas.", "Prueba con la rampa media o la larga.")),
        Cfg(4, "El cofre del laboratorio", "Un cofre pesado necesita la rampa adecuada.",
            0.8f, 1.3f, listOf(1.2f, 2.2f, 3.4f), 0.65f, 3,
            listOf("La rampa corta nunca funciona con cargas tan pesadas.", "Piensa en cuál rampa reparte mejor el esfuerzo en más distancia.")),
        Cfg(5, "La roca del cráter", "Esta roca es todavía más pesada.",
            0.9f, 1.4f, listOf(1.3f, 2.4f, 3.8f), 0.7f, 2,
            listOf("Con cargas tan pesadas, la rampa larga es casi siempre la mejor opción.", "Descarta primero la rampa corta.")),
        Cfg(6, "Desafío: Ingeniera Rampa", "La Ingeniera Rampa preparó la carga más difícil de subir.",
            1.0f, 1.5f, listOf(1.4f, 2.6f, 4.2f), 0.75f, 2,
            listOf("Este nivel exige elegir con precisión: compara las tres opciones antes de decidir.", "La rampa larga es la única que pide poco esfuerzo aquí."))
    )

    val bundles: List<LevelBundle> = configs.map { c ->
        val levelId = WORLD * 100 + c.n
        val objects = mutableListOf(
            LevelObject(levelId = levelId, objectType = LevelObjectType.CARGA, positionX = 0.1f, positionY = 0.9f, extraValue = c.loadWeight)
        )
        val rampLabels = listOf("Rampa corta", "Rampa media", "Rampa larga")
        c.lengths.forEachIndexed { idx, length ->
            objects += LevelObject(
                levelId = levelId, objectType = LevelObjectType.RAMPA,
                positionX = length, positionY = c.height, extraValue = length, extraLabel = rampLabels[idx]
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
                LevelRule(levelId = levelId, ruleType = LevelRuleType.ESFUERZO_MAXIMO_DISPONIBLE, value1 = c.maxEffort),
                LevelRule(levelId = levelId, ruleType = LevelRuleType.INTENTOS_MAXIMOS_PARA_ESTRELLA, value1 = c.maxAttemptsForStar.toFloat())
            ),
            hints = c.hints.mapIndexed { idx, text -> Hint(levelId = levelId, order = idx + 1, text = text) }
        )
    }
}
