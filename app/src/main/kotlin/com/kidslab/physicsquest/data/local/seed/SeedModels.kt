package com.kidslab.physicsquest.data.local.seed

import com.kidslab.physicsquest.data.local.entity.Hint
import com.kidslab.physicsquest.data.local.entity.Level
import com.kidslab.physicsquest.data.local.entity.LevelObject
import com.kidslab.physicsquest.data.local.entity.LevelRule

/** Agrupa todo lo necesario para insertar un nivel completo en una sola pasada. */
internal data class LevelBundle(
    val level: Level,
    val objects: List<LevelObject>,
    val rules: List<LevelRule>,
    val hints: List<Hint>
)
