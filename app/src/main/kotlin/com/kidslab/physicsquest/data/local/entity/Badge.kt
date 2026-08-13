package com.kidslab.physicsquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Insignia coleccionable que se muestra en el Inventario. */
@Entity(tableName = "badge")
data class Badge(
    @PrimaryKey val id: Long,
    val code: String,
    val title: String,
    val description: String,
    val criteriaDescription: String
)
