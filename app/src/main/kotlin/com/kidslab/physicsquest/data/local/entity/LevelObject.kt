package com.kidslab.physicsquest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kidslab.physicsquest.domain.model.LevelObjectType

/**
 * Un objeto colocado dentro de un nivel: la pelota, la meta, el punto de
 * apoyo de una palanca, una rampa, etc. Las posiciones están normalizadas
 * en el rango 0f..1f para poder dibujarse en cualquier tamaño de pantalla.
 */
@Entity(
    tableName = "level_object",
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
data class LevelObject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val levelId: Long,
    val objectType: LevelObjectType,
    val positionX: Float,
    val positionY: Float,
    val extraValue: Float? = null,
    val extraLabel: String? = null
)
