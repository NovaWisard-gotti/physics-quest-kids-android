package com.kidslab.physicsquest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Desafío "jefe científico" que cierra cada mundo, combinando el puzzle
 * principal del mundo con un segundo concepto ya aprendido en un mundo
 * anterior. Se desbloquea al reunir [starsRequiredToUnlock] estrellas
 * dentro del mundo.
 */
@Entity(
    tableName = "boss_challenge",
    foreignKeys = [
        ForeignKey(
            entity = World::class,
            parentColumns = ["id"],
            childColumns = ["worldId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Level::class,
            parentColumns = ["id"],
            childColumns = ["basePuzzleLevelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("worldId"), Index("basePuzzleLevelId")]
)
data class BossChallenge(
    @PrimaryKey val id: Long,
    val worldId: Long,
    val basePuzzleLevelId: Long,
    val title: String,
    val scientistName: String,
    val introDialogue: String,
    val mixedConceptsDescription: String,
    val starsRequiredToUnlock: Int,
    val rewardBadgeId: Long?
)
