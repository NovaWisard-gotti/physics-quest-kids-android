package com.kidslab.physicsquest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kidslab.physicsquest.data.local.dao.AttemptDao
import com.kidslab.physicsquest.data.local.dao.BadgeDao
import com.kidslab.physicsquest.data.local.dao.BossChallengeDao
import com.kidslab.physicsquest.data.local.dao.ConceptCardDao
import com.kidslab.physicsquest.data.local.dao.HintDao
import com.kidslab.physicsquest.data.local.dao.LevelDao
import com.kidslab.physicsquest.data.local.dao.LevelProgressDao
import com.kidslab.physicsquest.data.local.dao.UserBadgeDao
import com.kidslab.physicsquest.data.local.dao.UserProfileDao
import com.kidslab.physicsquest.data.local.dao.WorldDao
import com.kidslab.physicsquest.data.local.entity.Attempt
import com.kidslab.physicsquest.data.local.entity.Badge
import com.kidslab.physicsquest.data.local.entity.BossChallenge
import com.kidslab.physicsquest.data.local.entity.ConceptCard
import com.kidslab.physicsquest.data.local.entity.Hint
import com.kidslab.physicsquest.data.local.entity.Level
import com.kidslab.physicsquest.data.local.entity.LevelObject
import com.kidslab.physicsquest.data.local.entity.LevelProgress
import com.kidslab.physicsquest.data.local.entity.LevelRule
import com.kidslab.physicsquest.data.local.entity.UserBadge
import com.kidslab.physicsquest.data.local.entity.UserProfile
import com.kidslab.physicsquest.data.local.entity.World

/**
 * Base de datos local de Física Quest. 100% offline: no hay ningún
 * componente de red ni sincronización en la nube. Todo el progreso del
 * explorador vive únicamente en este dispositivo.
 */
@Database(
    entities = [
        UserProfile::class,
        World::class,
        Level::class,
        LevelObject::class,
        LevelRule::class,
        LevelProgress::class,
        Attempt::class,
        Hint::class,
        ConceptCard::class,
        BossChallenge::class,
        Badge::class,
        UserBadge::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PhysicsQuestDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun worldDao(): WorldDao
    abstract fun levelDao(): LevelDao
    abstract fun levelProgressDao(): LevelProgressDao
    abstract fun attemptDao(): AttemptDao
    abstract fun hintDao(): HintDao
    abstract fun conceptCardDao(): ConceptCardDao
    abstract fun bossChallengeDao(): BossChallengeDao
    abstract fun badgeDao(): BadgeDao
    abstract fun userBadgeDao(): UserBadgeDao

    companion object {
        private const val DATABASE_NAME = "physics_quest.db"

        @Volatile
        private var INSTANCE: PhysicsQuestDatabase? = null

        fun getInstance(context: Context): PhysicsQuestDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PhysicsQuestDatabase::class.java,
                    DATABASE_NAME
                ).build().also { INSTANCE = it }
            }
    }
}
