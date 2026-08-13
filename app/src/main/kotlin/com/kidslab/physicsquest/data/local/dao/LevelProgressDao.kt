package com.kidslab.physicsquest.data.local.dao

import androidx.room.*
import com.kidslab.physicsquest.data.local.entity.LevelProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelProgressDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(progress: LevelProgress): Long

    @Update
    suspend fun update(progress: LevelProgress)

    @Query("SELECT * FROM level_progress WHERE userProfileId = :userProfileId AND levelId = :levelId LIMIT 1")
    suspend fun getForLevel(userProfileId: Long, levelId: Long): LevelProgress?

    @Query("SELECT * FROM level_progress WHERE userProfileId = :userProfileId")
    fun observeAllForUser(userProfileId: Long): Flow<List<LevelProgress>>

    @Query("SELECT * FROM level_progress WHERE userProfileId = :userProfileId")
    suspend fun getAllForUserOnce(userProfileId: Long): List<LevelProgress>

    @Query(
        """
        SELECT COALESCE(SUM(lp.stars), 0) FROM level_progress lp
        INNER JOIN level l ON l.id = lp.levelId
        WHERE lp.userProfileId = :userProfileId AND l.worldId = :worldId
        """
    )
    suspend fun sumStarsForWorld(userProfileId: Long, worldId: Long): Int

    @Query("SELECT COALESCE(SUM(stars), 0) FROM level_progress WHERE userProfileId = :userProfileId")
    fun observeTotalStars(userProfileId: Long): Flow<Int>
}
