package com.kidslab.physicsquest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kidslab.physicsquest.data.local.entity.Badge
import com.kidslab.physicsquest.data.local.entity.UserBadge
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(badges: List<Badge>)

    @Query("SELECT * FROM badge ORDER BY id ASC")
    fun observeAll(): Flow<List<Badge>>

    @Query("SELECT COUNT(*) FROM badge")
    suspend fun count(): Int
}

@Dao
interface UserBadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userBadge: UserBadge): Long

    @Query("SELECT * FROM user_badge WHERE userProfileId = :userProfileId")
    fun observeForUser(userProfileId: Long): Flow<List<UserBadge>>

    @Query("SELECT EXISTS(SELECT 1 FROM user_badge WHERE userProfileId = :userProfileId AND badgeId = :badgeId)")
    suspend fun hasBadge(userProfileId: Long, badgeId: Long): Boolean
}
