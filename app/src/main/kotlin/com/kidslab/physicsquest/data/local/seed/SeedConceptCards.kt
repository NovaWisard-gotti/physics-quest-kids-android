package com.kidslab.physicsquest.data.local.seed

import com.kidslab.physicsquest.data.local.entity.ConceptCard

/** 25 tarjetas de concepto (5 por mundo), pensadas para leerse en menos de 30 segundos. */
internal object SeedConceptCards {
    private fun card(worldId: Long, order: Int, title: String, explanation: String, example: String) =
        ConceptCard(id = worldId * 100 + order, worldId = worldId, order = order, title = title, shortExplanation = explanation, everydayExample = example)

    val all: List<ConceptCard> = listOf(
        // Mundo 1: Movimiento
        card(SeedWorlds.MOVIMIENTO, 1, "Trayectoria", "El camino que sigue un objeto cuando se mueve por el aire.", "La curva que dibuja un balón cuando lo pateas."),
        card(SeedWorlds.MOVIMIENTO, 2, "Ángulo de lanzamiento", "La inclinación con la que sale disparado un objeto.", "Lanzar una piedra bien alto o bien plano cambia dónde cae."),
        card(SeedWorlds.MOVIMIENTO, 3, "Fuerza del lanzamiento", "Cuánto empuje le das a algo al lanzarlo.", "Lanzar una pelota suave o con todas tus fuerzas."),
        card(SeedWorlds.MOVIMIENTO, 4, "Gravedad", "La fuerza que siempre tira de las cosas hacia el suelo.", "Por eso una pelota lanzada al aire siempre termina cayendo."),
        card(SeedWorlds.MOVIMIENTO, 5, "Obstáculos en el camino", "Un objeto en medio de una trayectoria puede desviarla o detenerla.", "Esquivar un árbol al lanzar una pelota."),
        // Mundo 2: Fuerzas
        card(SeedWorlds.FUERZAS, 1, "Palanca", "Una barra que gira sobre un punto de apoyo para mover cosas con menos esfuerzo.", "Un sube y baja del parque es una palanca."),
        card(SeedWorlds.FUERZAS, 2, "Punto de apoyo", "El lugar donde la palanca se apoya y gira.", "En unas tijeras, el tornillo del centro es el punto de apoyo."),
        card(SeedWorlds.FUERZAS, 3, "Brazo corto y brazo largo", "Las dos partes de la palanca a cada lado del punto de apoyo.", "En un cascanueces, el brazo largo permite romper la nuez con poca fuerza."),
        card(SeedWorlds.FUERZAS, 4, "Ventaja mecánica", "Cómo una palanca permite mover algo pesado usando menos fuerza.", "Levantar una roca grande con una barra larga."),
        card(SeedWorlds.FUERZAS, 5, "Equilibrio de fuerzas", "Cuando las fuerzas a ambos lados de la palanca se compensan.", "Dos amigos de distinto peso pueden equilibrar un sube y baja moviéndose."),
        // Mundo 3: Máquinas simples
        card(SeedWorlds.MAQUINAS_SIMPLES, 1, "Plano inclinado", "Una superficie inclinada que ayuda a subir cosas pesadas.", "La rampa de una acera para subir con la bicicleta."),
        card(SeedWorlds.MAQUINAS_SIMPLES, 2, "Esfuerzo", "La cantidad de fuerza que hace falta para mover algo.", "Empujar un carrito cuesta arriba requiere más esfuerzo."),
        card(SeedWorlds.MAQUINAS_SIMPLES, 3, "Longitud de la rampa", "Una rampa más larga para la misma altura pide menos esfuerzo.", "Es más fácil subir una maleta por una rampa larga que por una corta y empinada."),
        card(SeedWorlds.MAQUINAS_SIMPLES, 4, "Máquinas simples", "Herramientas sencillas que facilitan el trabajo, como rampas, palancas y ruedas.", "Un destornillador, una polea o una cuña son máquinas simples."),
        card(SeedWorlds.MAQUINAS_SIMPLES, 5, "Comparar opciones", "Elegir la mejor herramienta según cuánto esfuerzo se quiere ahorrar.", "Elegir entre varias rampas para subir una carga."),
        // Mundo 4: Energía
        card(SeedWorlds.ENERGIA, 1, "Energía potencial", "La energía que tiene un objeto por estar en alto.", "Una pelota en la punta de un tobogán tiene energía potencial."),
        card(SeedWorlds.ENERGIA, 2, "Energía cinética", "La energía que tiene un objeto por estar en movimiento.", "Una bicicleta bajando una cuesta gana energía cinética."),
        card(SeedWorlds.ENERGIA, 3, "Conservación de la energía", "La energía no desaparece: se transforma de un tipo a otro.", "La altura de un tobogán se convierte en velocidad al bajar."),
        card(SeedWorlds.ENERGIA, 4, "Altura y velocidad", "Cuanto más alto empieza un recorrido, más velocidad se puede ganar al bajar.", "Una montaña rusa parte siempre desde el punto más alto."),
        card(SeedWorlds.ENERGIA, 5, "Fricción", "Una fuerza que frena el movimiento poco a poco.", "Por eso una pelota que rueda por el suelo termina deteniéndose."),
        // Mundo 5: Sonido y ondas
        card(SeedWorlds.SONIDO_Y_ONDAS, 1, "Onda sonora", "El sonido viaja en forma de ondas a través del aire.", "Como los círculos que se forman al tirar una piedra al agua."),
        card(SeedWorlds.SONIDO_Y_ONDAS, 2, "Frecuencia", "Qué tan rápido vibra una onda de sonido.", "Una frecuencia alta suena agudo, como un silbato."),
        card(SeedWorlds.SONIDO_Y_ONDAS, 3, "Grave y agudo", "Los sonidos graves tienen frecuencia baja; los agudos, frecuencia alta.", "La voz de un contrabajo es grave; la de un flautín es aguda."),
        card(SeedWorlds.SONIDO_Y_ONDAS, 4, "Amplitud", "Qué tan grande es una onda de sonido, relacionada con el volumen.", "Hablar en voz baja o gritar cambia la amplitud del sonido."),
        card(SeedWorlds.SONIDO_Y_ONDAS, 5, "Suave y fuerte", "Un sonido de amplitud baja es suave; uno de amplitud alta es fuerte.", "Un susurro es suave; una sirena es fuerte.")
    )
}
