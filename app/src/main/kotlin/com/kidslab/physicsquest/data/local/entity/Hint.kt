package com.kidslab.physicsquest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Pista de texto asociada a un nivel. Se desbloquea después de 2 intentos
 * fallidos y NUNCA resta estrellas ni puntos por usarla.
 */
@Entity(
    tableName = "hint",
    foreignKeys = [
        ForeignKey(
            entity = Level::class,
            parentColumns = ["id"],
            childColumns = ["levelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("levelId")]
)
data class Hint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val levelId: Long,
    val order: Int,
    val text: String
)
