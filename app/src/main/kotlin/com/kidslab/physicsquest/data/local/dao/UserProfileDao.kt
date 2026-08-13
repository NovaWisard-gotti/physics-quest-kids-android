package com.kidslab.physicsquest.data.local.dao

import androidx.room.*
import com.kidslab.physicsquest.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfile): Long

    @Update
    suspend fun update(profile: UserProfile)

    @Query("SELECT * FROM user_profile LIMIT 1")
    fun observeProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getProfileOnce(): UserProfile?

    @Query("UPDATE user_profile SET totalStars = :stars WHERE id = :id")
    suspend fun updateTotalStars(id: Long, stars: Int)

    @Query("UPDATE user_profile SET shipPiecesRecovered = shipPiecesRecovered + 1 WHERE id = :id")
    suspend fun incrementShipPieces(id: Long)
}
