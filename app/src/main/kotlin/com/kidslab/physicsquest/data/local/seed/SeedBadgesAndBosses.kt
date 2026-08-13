package com.kidslab.physicsquest.data.local.seed

import com.kidslab.physicsquest.data.local.entity.Badge
import com.kidslab.physicsquest.data.local.entity.BossChallenge

/** 8 insignias coleccionables, visibles en la pantalla de Inventario. */
internal object SeedBadges {
    const val PRIMER_LANZAMIENTO = 1L
    const val EXPLORADOR_MOVIMIENTO = 2L
    const val MAESTRO_PALANCA = 3L
    const val INGENIERO_RAMPAS = 4L
    const val PILOTO_ENERGIA = 5L
    const val OIDO_DE_ORO = 6L
    const val COLECCIONISTA_CONCEPTOS = 7L
    const val HEROE_DE_FISICA_QUEST = 8L

    val all = listOf(
        Badge(PRIMER_LANZAMIENTO, "primer_lanzamiento", "Primer lanzamiento", "Completaste tu primer nivel de Física Quest.", "Termina cualquier nivel por primera vez."),
        Badge(EXPLORADOR_MOVIMIENTO, "explorador_movimiento", "Explorador del Movimiento", "Superaste todos los niveles del mundo Movimiento.", "Completa los 6 niveles del Mundo 1."),
        Badge(MAESTRO_PALANCA, "maestro_palanca", "Maestro de la Palanca", "Superaste todos los niveles del mundo Fuerzas.", "Completa los 6 niveles del Mundo 2."),
        Badge(INGENIERO_RAMPAS, "ingeniero_rampas", "Ingeniero de Rampas", "Superaste todos los niveles del mundo Máquinas simples.", "Completa los 6 niveles del Mundo 3."),
        Badge(PILOTO_ENERGIA, "piloto_energia", "Piloto de la Energía", "Superaste todos los niveles del mundo Energía.", "Completa los 6 niveles del Mundo 4."),
        Badge(OIDO_DE_ORO, "oido_de_oro", "Oído de Oro", "Superaste todos los niveles del mundo Sonido y ondas.", "Completa los 6 niveles del Mundo 5."),
        Badge(COLECCIONISTA_CONCEPTOS, "coleccionista_conceptos", "Coleccionista de Conceptos", "Desbloqueaste las 25 tarjetas de concepto.", "Consigue las 25 tarjetas de concepto del juego."),
        Badge(HEROE_DE_FISICA_QUEST, "heroe_fisica_quest", "Héroe de Física Quest", "Reconstruiste la nave completa: ¡terminaste la aventura!", "Recupera las 5 piezas de la nave venciendo a los 5 jefes científicos.")
    )
}

/** Los 5 desafíos "jefe científico" que cierran cada mundo. */
internal object SeedBossChallenges {
    val all = listOf(
        BossChallenge(
            id = SeedWorlds.MOVIMIENTO, worldId = SeedWorlds.MOVIMIENTO, basePuzzleLevelId = SeedWorlds.MOVIMIENTO * 100 + 6,
            title = "El desafío de la Dra. Inercia", scientistName = "Dra. Inercia",
            introDialogue = "\"Todo objeto en movimiento sigue su camino... ¡a menos que tú decidas lo contrario! Demuéstrame que dominas dirección y fuerza a la vez.\"",
            mixedConceptsDescription = "Combina ángulo y fuerza de lanzamiento en un recorrido con tres obstáculos, exigiendo más precisión que cualquier nivel anterior.",
            starsRequiredToUnlock = 9, rewardBadgeId = SeedBadges.EXPLORADOR_MOVIMIENTO
        ),
        BossChallenge(
            id = SeedWorlds.FUERZAS, worldId = SeedWorlds.FUERZAS, basePuzzleLevelId = SeedWorlds.FUERZAS * 100 + 6,
            title = "El desafío del Profesor Torque", scientistName = "Profesor Torque",
            introDialogue = "\"Con la palanca correcta se mueve el mundo. Encuentra el punto de apoyo exacto para mi carga más pesada.\"",
            mixedConceptsDescription = "Aplica ventaja mecánica y equilibrio de torques al mismo tiempo con la carga más pesada del mundo.",
            starsRequiredToUnlock = 9, rewardBadgeId = SeedBadges.MAESTRO_PALANCA
        ),
        BossChallenge(
            id = SeedWorlds.MAQUINAS_SIMPLES, worldId = SeedWorlds.MAQUINAS_SIMPLES, basePuzzleLevelId = SeedWorlds.MAQUINAS_SIMPLES * 100 + 6,
            title = "El desafío de la Ingeniera Rampa", scientistName = "Ingeniera Rampa",
            introDialogue = "\"Toda máquina simple tiene un secreto: el camino más largo a veces es el más fácil. Elige bien.\"",
            mixedConceptsDescription = "Compara tres rampas para la carga más pesada, combinando la idea de esfuerzo con la de distancia recorrida.",
            starsRequiredToUnlock = 9, rewardBadgeId = SeedBadges.INGENIERO_RAMPAS
        ),
        BossChallenge(
            id = SeedWorlds.ENERGIA, worldId = SeedWorlds.ENERGIA, basePuzzleLevelId = SeedWorlds.ENERGIA * 100 + 6,
            title = "El desafío del Doctor Kinético", scientistName = "Doctor Kinético",
            introDialogue = "\"La energía nunca se pierde, solo se transforma. Arma el recorrido más largo del reino y demuéstralo.\"",
            mixedConceptsDescription = "Encadena cinco tramos combinando energía potencial y cinética en un solo recorrido.",
            starsRequiredToUnlock = 9, rewardBadgeId = SeedBadges.PILOTO_ENERGIA
        ),
        BossChallenge(
            id = SeedWorlds.SONIDO_Y_ONDAS, worldId = SeedWorlds.SONIDO_Y_ONDAS, basePuzzleLevelId = SeedWorlds.SONIDO_Y_ONDAS * 100 + 6,
            title = "El desafío de la Maestra Resonancia", scientistName = "Maestra Resonancia",
            introDialogue = "\"Un buen oído distingue frecuencia y amplitud a la vez. Ajusta ambos controles con precisión y la puerta se abrirá.\"",
            mixedConceptsDescription = "Exige ajustar frecuencia y amplitud de forma simultánea, dentro de un rango muy angosto.",
            starsRequiredToUnlock = 9, rewardBadgeId = SeedBadges.OIDO_DE_ORO
        )
    )
}
