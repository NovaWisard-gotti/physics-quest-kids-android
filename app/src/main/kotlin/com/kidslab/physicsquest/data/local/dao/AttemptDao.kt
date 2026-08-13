package com.kidslab.physicsquest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kidslab.physicsquest.data.local.entity.Attempt

@Dao
interface AttemptDao {
    @Insert
    suspend fun insert(attempt: Attempt): Long

    @Query("SELECT * FROM attempt WHERE levelProgressId = :levelProgressId ORDER BY attemptNumber ASC")
    suspend fun getAllForProgress(levelProgressId: Long): List<Attempt>

    @Query("SELECT COUNT(*) FROM attempt WHERE levelProgressId = :levelProgressId")
    suspend fun countForProgress(levelProgressId: Long): Int

    @Query("SELECT COUNT(*) FROM attempt WHERE levelProgressId = :levelProgressId AND success = 0")
    suspend fun countFailuresForProgress(levelProgressId: Long): Int
}
