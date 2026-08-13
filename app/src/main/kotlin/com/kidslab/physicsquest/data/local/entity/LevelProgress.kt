package com.kidslab.physicsquest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Progreso de un usuario en un nivel concreto. Los fallos nunca se
 * penalizan de forma permanente: solo se guarda el MEJOR resultado
 * obtenido (mayor número de estrellas, menor número de intentos).
 */
@Entity(
    tableName = "level_progress",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["id"],
            childColumns = ["userProfileId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Level::class,
            parentColumns = ["id"],
            childColumns = ["levelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userProfileId"), Index("levelId"), Index(value = ["userProfileId", "levelId"], unique = true)]
)
data class LevelProgress(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userProfileId: Long,
    val levelId: Long,
    val stars: Int = 0, // 0..3, siempre el mejor resultado histórico
    val bestAttemptsToComplete: Int? = null,
    val completed: Boolean = false,
    val unlocked: Boolean = false,
    val lastPlayedAtEpochMillis: Long? = null
)
