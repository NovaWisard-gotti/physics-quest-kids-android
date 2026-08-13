package com.kidslab.physicsquest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kidslab.physicsquest.domain.model.LevelRuleType

/**
 * Una regla de evaluación de un nivel: tolerancias de meta, intentos
 * máximos para ganar la estrella de "pocos intentos", umbral de
 * eficiencia, rangos válidos de frecuencia/amplitud, etc.
 */
@Entity(
    tableName = "level_rule",
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
data class LevelRule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val levelId: Long,
    val ruleType: LevelRuleType,
    val value1: Float,
    val value2: Float? = null
)
