package com.kidslab.physicsquest.data.local.dao

import androidx.room.*
import com.kidslab.physicsquest.data.local.entity.World
import kotlinx.coroutines.flow.Flow

@Dao
interface WorldDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(worlds: List<World>)

    @Query("SELECT * FROM world ORDER BY `order` ASC")
    fun observeAll(): Flow<List<World>>

    @Query("SELECT * FROM world WHERE id = :id")
    suspend fun getById(id: Long): World?

    @Query("SELECT COUNT(*) FROM world")
    suspend fun count(): Int
}
