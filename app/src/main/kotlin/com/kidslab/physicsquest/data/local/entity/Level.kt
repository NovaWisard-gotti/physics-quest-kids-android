package com.kidslab.physicsquest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Un nivel jugable dentro de un mundo. Los parámetros concretos del
 * puzzle (posiciones, metas, tolerancias) viven en [LevelObject] y
 * [LevelRule] para poder añadir niveles sin tocar el código de las
 * pantallas de Compose.
 */
@Entity(
    tableName = "level",
    foreignKeys = [
        ForeignKey(
            entity = World::class,
            parentColumns = ["id"],
            childColumns = ["worldId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("worldId"), Index("conceptCardId")]
)
data class Level(
    @PrimaryKey val id: Long,
    val worldId: Long,
    val levelNumberInWorld: Int, // 1..6
    val title: String,
    val instructions: String,
    val difficulty: Int, // 1..3
    val conceptCardId: Long?
)
