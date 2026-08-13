package com.kidslab.physicsquest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Cada intento individual que hace el jugador en un nivel. Se guarda el
 * historial completo (no solo el mejor) para poder calcular estadísticas
 * y para saber cuándo desbloquear una pista (a partir del 2º intento).
 */
@Entity(
    tableName = "attempt",
    foreignKeys = [
        ForeignKey(
            entity = LevelProgress::class,
            parentColumns = ["id"],
            childColumns = ["levelProgressId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("levelProgressId")]
)
data class Attempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val levelProgressId: Long,
    val attemptNumber: Int,
    val success: Boolean,
    val efficiencyScore: Float, // 0f..1f
    val starsEarned: Int,
    val hintUsed: Boolean,
    val timestampEpochMillis: Long
)
