package com.kidslab.physicsquest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kidslab.physicsquest.data.local.entity.Hint

@Dao
interface HintDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(hints: List<Hint>)

    @Query("SELECT * FROM hint WHERE levelId = :levelId ORDER BY `order` ASC")
    suspend fun getForLevel(levelId: Long): List<Hint>
}
