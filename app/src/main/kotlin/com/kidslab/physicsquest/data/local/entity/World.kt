package com.kidslab.physicsquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kidslab.physicsquest.domain.model.PuzzleType

/** Uno de los cinco mundos del mapa de aventura. */
@Entity(tableName = "world")
data class World(
    @PrimaryKey val id: Long,
    val order: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val puzzleType: PuzzleType,
    val starsRequiredToUnlock: Int,
    val shipPieceName: String
)
