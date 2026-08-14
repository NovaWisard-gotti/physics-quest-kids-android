package com.kidslab.physicsquest.data.local.dao

import androidx.room.*
import com.kidslab.physicsquest.data.local.entity.Level
import com.kidslab.physicsquest.data.local.entity.LevelObject
import com.kidslab.physicsquest.data.local.entity.LevelRule
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLevels(levels: List<Level>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertObjects(objects: List<LevelObject>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRules(rules: List<LevelRule>)

    @Query("SELECT * FROM level WHERE worldId = :worldId ORDER BY levelNumberInWorld ASC")
    fun observeLevelsForWorld(worldId: Long): Flow<List<Level>>

    @Query("SELECT * FROM level")
    fun observeAll(): Flow<List<Level>>

    @Query("SELECT * FROM level WHERE id = :levelId")
    suspend fun getLevel(levelId: Long): Level?

    @Query("SELECT * FROM level_object WHERE levelId = :levelId")
    suspend fun getObjectsForLevel(levelId: Long): List<LevelObject>

    @Query("SELECT * FROM level_rule WHERE levelId = :levelId")
    suspend fun getRulesForLevel(levelId: Long): List<LevelRule>

    @Query("SELECT COUNT(*) FROM level")
    suspend fun count(): Int
}
