package com.kidslab.physicsquest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Relación N:M entre un perfil y las insignias que ha ganado. */
@Entity(
    tableName = "user_badge",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["id"],
            childColumns = ["userProfileId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Badge::class,
            parentColumns = ["id"],
            childColumns = ["badgeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userProfileId"), Index("badgeId"), Index(value = ["userProfileId", "badgeId"], unique = true)]
)
data class UserBadge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userProfileId: Long,
    val badgeId: Long,
    val earnedAtEpochMillis: Long
)
