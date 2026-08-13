package com.kidslab.physicsquest.data.local

import androidx.room.TypeConverter
import com.kidslab.physicsquest.domain.model.LevelObjectType
import com.kidslab.physicsquest.domain.model.LevelRuleType
import com.kidslab.physicsquest.domain.model.PuzzleType

/** Convierte los enums del dominio a texto plano para guardarlos en Room. */
class Converters {
    @TypeConverter
    fun fromPuzzleType(value: PuzzleType): String = value.name

    @TypeConverter
    fun toPuzzleType(value: String): PuzzleType = PuzzleType.valueOf(value)

    @TypeConverter
    fun fromLevelObjectType(value: LevelObjectType): String = value.name

    @TypeConverter
    fun toLevelObjectType(value: String): LevelObjectType = LevelObjectType.valueOf(value)

    @TypeConverter
    fun fromLevelRuleType(value: LevelRuleType): String = value.name

    @TypeConverter
    fun toLevelRuleType(value: String): LevelRuleType = LevelRuleType.valueOf(value)
}
