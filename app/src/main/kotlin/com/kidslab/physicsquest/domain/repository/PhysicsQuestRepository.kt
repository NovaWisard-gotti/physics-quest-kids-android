package com.kidslab.physicsquest.domain.repository

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
import com.kidslab.physicsquest.domain.model.PuzzleResult
import kotlinx.coroutines.flow.Flow

/**
 * Puerta de entrada única a los datos de Física Quest. Las pantallas y
 * ViewModels solo hablan con esta interfaz, nunca con Room directamente,
 * lo que facilita las pruebas (se puede usar una implementación falsa).
 */
interface PhysicsQuestRepository {
    // --- Perfil ---
    fun observeProfile(): Flow<UserProfile?>
    suspend fun getOrCreateProfile(defaultName: String): UserProfile
    suspend fun renameProfile(profileId: Long, newName: String)

    // --- Mundos y niveles ---
    fun observeWorlds(): Flow<List<World>>
    suspend fun getWorld(worldId: Long): World?
    fun observeLevelsForWorld(worldId: Long): Flow<List<Level>>
    suspend fun getLevel(levelId: Long): Level?
    suspend fun getObjectsForLevel(levelId: Long): List<LevelObject>
    suspend fun getRulesForLevel(levelId: Long): List<LevelRule>
    suspend fun getHintsForLevel(levelId: Long): List<Hint>

    // --- Progreso ---
    fun observeProgressForUser(userProfileId: Long): Flow<List<LevelProgress>>
    suspend fun getProgress(userProfileId: Long, levelId: Long): LevelProgress?
    suspend fun sumStarsForWorld(userProfileId: Long, worldId: Long): Int
    fun observeTotalStars(userProfileId: Long): Flow<Int>

    /**
     * Registra un intento de un nivel y actualiza el progreso guardando
     * siempre el MEJOR resultado histórico (nunca se penalizan los fallos).
     */
    suspend fun recordAttempt(
        userProfileId: Long,
        levelId: Long,
        result: PuzzleResult,
        attemptNumberInSession: Int,
        hintUsed: Boolean
    ): Attempt

    suspend fun countFailedAttempts(userProfileId: Long, levelId: Long): Int

    // --- Conceptos, jefes e insignias ---
    fun observeConceptCards(): Flow<List<ConceptCard>>
    suspend fun getBossChallengeForWorld(worldId: Long): BossChallenge?
    fun observeBadges(): Flow<List<Badge>>
    fun observeUserBadges(userProfileId: Long): Flow<List<UserBadge>>
    suspend fun awardBadgeIfNeeded(userProfileId: Long, badgeId: Long)

    /** Asegura que la base de datos tenga los 5 mundos y 30 niveles cargados (solo la primera vez). */
    suspend fun ensureSeeded()
}
