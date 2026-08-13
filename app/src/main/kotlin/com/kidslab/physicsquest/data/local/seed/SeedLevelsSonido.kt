package com.kidslab.physicsquest.data.local.seed

import com.kidslab.physicsquest.data.local.entity.Hint
import com.kidslab.physicsquest.data.local.entity.Level
import com.kidslab.physicsquest.data.local.entity.LevelObject
import com.kidslab.physicsquest.data.local.entity.LevelRule
import com.kidslab.physicsquest.domain.model.LevelObjectType
import com.kidslab.physicsquest.domain.model.LevelRuleType

/**
 * Mundo 5: Sonido y ondas — puzzle de Sonido.
 * El jugador mueve dos controles (frecuencia y amplitud) hasta que el
 * sonido generado localmente entra dentro del rango pedido por la
 * "puerta sónica" de cada nivel.
 */
internal object SeedLevelsSonido {
    private const val WORLD = SeedWorlds.SONIDO_Y_ONDAS

    private data class Cfg(
        val n: Int, val title: String, val instructions: String,
        val minFreq: Float, val maxFreq: Float, val minAmp: Float, val maxAmp: Float,
        val hints: List<String>
    )

    private val configs = listOf(
        Cfg(1, "Sonido grave y suave", "La primera puerta se abre con un sonido grave y suave.",
            100f, 300f, 0.1f, 0.4f,
            listOf("Grave significa frecuencia baja: mueve el control hacia la izquierda.", "Suave significa poco volumen: no subas demasiado la amplitud.")),
        Cfg(2, "Sonido agudo y suave", "Ahora la puerta pide un sonido agudo pero suave.",
            900f, 1400f, 0.1f, 0.4f,
            listOf("Agudo significa frecuencia alta: mueve el control hacia la derecha.", "Mantén el volumen bajo, como en el nivel anterior.")),
        Cfg(3, "Sonido grave y fuerte", "Esta puerta necesita un sonido grave pero bien fuerte.",
            100f, 300f, 0.6f, 0.9f,
            listOf("Vuelve a una frecuencia baja, como en el primer nivel.", "Esta vez sube bastante el volumen.")),
        Cfg(4, "Sonido agudo y fuerte", "Un sonido agudo y fuerte abrirá esta puerta.",
            1200f, 1700f, 0.6f, 0.9f,
            listOf("Combina una frecuencia alta con un volumen alto.", "Prueba cerca del máximo en ambos controles.")),
        Cfg(5, "Ajuste fino", "El rango permitido es más angosto: ajusta con precisión.",
            700f, 1000f, 0.35f, 0.55f,
            listOf("Este nivel pide un sonido intermedio en ambos controles.", "Mueve los controles poco a poco hasta encontrar el rango exacto.")),
        Cfg(6, "Desafío: Maestra Resonancia", "La Maestra Resonancia solo abrirá la puerta con un sonido muy agudo y muy fuerte, dentro de un rango exacto.",
            1500f, 1800f, 0.75f, 0.95f,
            listOf("Necesitas frecuencia y amplitud altas al mismo tiempo, con poco margen de error.", "Ajusta primero la frecuencia y después afina la amplitud."))
    )

    val bundles: List<LevelBundle> = configs.map { c ->
        val levelId = WORLD * 100 + c.n
        LevelBundle(
            level = Level(
                id = levelId, worldId = WORLD, levelNumberInWorld = c.n, title = c.title,
                instructions = c.instructions, difficulty = ((c.n - 1) / 2) + 1,
                conceptCardId = WORLD * 100 + minOf(c.n, 5)
            ),
            objects = listOf(
                LevelObject(levelId = levelId, objectType = LevelObjectType.ALTAVOZ, positionX = 0.5f, positionY = 0.5f)
            ),
            rules = listOf(
                LevelRule(levelId = levelId, ruleType = LevelRuleType.RANGO_FRECUENCIA, value1 = c.minFreq, value2 = c.maxFreq),
                LevelRule(levelId = levelId, ruleType = LevelRuleType.RANGO_AMPLITUD, value1 = c.minAmp, value2 = c.maxAmp)
            ),
            hints = c.hints.mapIndexed { idx, text -> Hint(levelId = levelId, order = idx + 1, text = text) }
        )
    }
}
