package com.kidslab.physicsquest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Perfil del explorador/a. Física Quest no requiere login: se crea un
 * único perfil local la primera vez que se abre la app.
 */
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val explorerName: String,
    val avatarId: Int = 0,
    val totalStars: Int = 0,
    val shipPiecesRecovered: Int = 0,
    val createdAtEpochMillis: Long
)
