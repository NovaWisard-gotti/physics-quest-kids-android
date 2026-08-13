package com.kidslab.physicsquest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tarjeta de concepto: una explicación breve e ilustrada de una idea de
 * física, pensada para leerse en menos de 30 segundos. Se desbloquea al
 * completar el nivel asociado y queda guardada en el Inventario.
 */
@Entity(
    tableName = "concept_card",
    foreignKeys = [
        ForeignKey(
            entity = World::class,
            parentColumns = ["id"],
            childColumns = ["worldId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("worldId")]
)
data class ConceptCard(
    @PrimaryKey val id: Long,
    val worldId: Long,
    val order: Int,
    val title: String,
    val shortExplanation: String,
    val everydayExample: String
)
