package com.kidslab.physicsquest.data.local.seed

import com.kidslab.physicsquest.data.local.entity.Hint
import com.kidslab.physicsquest.data.local.entity.Level
import com.kidslab.physicsquest.data.local.entity.LevelObject
import com.kidslab.physicsquest.data.local.entity.LevelRule
import com.kidslab.physicsquest.domain.model.LevelObjectType
import com.kidslab.physicsquest.domain.model.LevelRuleType

/**
 * Mundo 2: Fuerzas — puzzle de Palanca.
 * La carga siempre está en el extremo izquierdo (posición 0) y el
 * explorador siempre empuja desde el extremo derecho (posición 1). El
 * jugador elige dónde poner el punto de apoyo y cuánta fuerza aplicar.
 */
internal object SeedLevelsFuerzas {
    private const val WORLD = SeedWorlds.FUERZAS

    private data class Cfg(
        val n: Int, val title: String, val instructions: String,
        val loadWeight: Float, val efficiencyMargin: Float, val maxAttemptsForStar: Int,
        val hints: List<String>
    )

    private val configs = listOf(
        Cfg(1, "La primera carga", "Mueve el punto de apoyo para levantar la caja con poco esfuerzo.", 0.3f, 0.6f, 3,
            listOf("Si acercas el punto de apoyo a la carga, necesitas menos fuerza.", "Prueba con el apoyo cerca del extremo izquierdo.")),
        Cfg(2, "Un poco más pesada", "Esta caja pesa más. Encuentra el punto de apoyo correcto.", 0.5f, 0.55f, 3,
            listOf("El punto de apoyo debe estar más cerca de la carga que del explorador.", "Con el apoyo alrededor del 20% del camino suele alcanzar.")),
        Cfg(3, "Piedra pesada", "Ahora es una piedra. ¡Usa la ventaja de la palanca!", 0.7f, 0.5f, 3,
            listOf("Recuerda: brazo corto del lado de la carga, brazo largo del lado del esfuerzo.", "Ajusta el apoyo poco a poco hacia la izquierda.")),
        Cfg(4, "El baúl del laboratorio", "El baúl es difícil de mover. Encuentra el equilibrio perfecto.", 0.9f, 0.45f, 3,
            listOf("Cuanto más pesada la carga, más cerca debe estar el apoyo de ella.", "Prueba con el apoyo entre el 15% y el 25% del camino.")),
        Cfg(5, "El bloque de metal", "Un bloque de metal muy pesado espera ser levantado.", 1.1f, 0.4f, 2,
            listOf("Necesitarás un apoyo bastante cercano a la carga.", "Combina un apoyo cercano con una fuerza generosa.")),
        Cfg(6, "Desafío: Profesor Torque", "El Profesor Torque preparó el bloque más pesado del taller.", 1.3f, 0.35f, 2,
            listOf("Este es el nivel más exigente: el apoyo debe estar muy cerca de la carga.", "Ajusta con cuidado: pequeños cambios en el apoyo cambian mucho el esfuerzo necesario."))
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
                LevelObject(levelId = levelId, objectType = LevelObjectType.CARGA, positionX = 0f, positionY = 0.5f, extraValue = c.loadWeight),
                LevelObject(levelId = levelId, objectType = LevelObjectType.APOYO_PALANCA, positionX = 0.5f, positionY = 0.5f)
            ),
            rules = listOf(
                LevelRule(levelId = levelId, ruleType = LevelRuleType.TOLERANCIA_EQUILIBRIO_TORQUE, value1 = c.efficiencyMargin),
                LevelRule(levelId = levelId, ruleType = LevelRuleType.INTENTOS_MAXIMOS_PARA_ESTRELLA, value1 = c.maxAttemptsForStar.toFloat())
            ),
            hints = c.hints.mapIndexed { idx, text -> Hint(levelId = levelId, order = idx + 1, text = text) }
        )
    }
}
