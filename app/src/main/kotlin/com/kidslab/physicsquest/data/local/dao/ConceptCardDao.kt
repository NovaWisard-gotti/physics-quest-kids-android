package com.kidslab.physicsquest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kidslab.physicsquest.data.local.entity.ConceptCard
import kotlinx.coroutines.flow.Flow

@Dao
interface ConceptCardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(cards: List<ConceptCard>)

    @Query("SELECT * FROM concept_card ORDER BY worldId ASC, `order` ASC")
    fun observeAll(): Flow<List<ConceptCard>>

    @Query("SELECT * FROM concept_card WHERE id = :id")
    suspend fun getById(id: Long): ConceptCard?

    @Query("SELECT COUNT(*) FROM concept_card")
    suspend fun count(): Int
}
