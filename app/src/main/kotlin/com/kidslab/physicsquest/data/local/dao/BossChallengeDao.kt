package com.kidslab.physicsquest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kidslab.physicsquest.data.local.entity.BossChallenge

@Dao
interface BossChallengeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(bosses: List<BossChallenge>)

    @Query("SELECT * FROM boss_challenge WHERE worldId = :worldId LIMIT 1")
    suspend fun getForWorld(worldId: Long): BossChallenge?

    @Query("SELECT COUNT(*) FROM boss_challenge")
    suspend fun count(): Int
}
