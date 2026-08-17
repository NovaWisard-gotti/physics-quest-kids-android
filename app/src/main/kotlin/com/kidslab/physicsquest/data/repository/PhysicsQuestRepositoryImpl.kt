package com.kidslab.physicsquest.data.repository

import com.kidslab.physicsquest.data.local.PhysicsQuestDatabase
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
import com.kidslab.physicsquest.data.local.seed.SeedBadges
import com.kidslab.physicsquest.data.local.seed.SeedDataProvider
import com.kidslab.physicsquest.domain.engine.StarCalculator
import com.kidslab.physicsquest.domain.model.PuzzleResult
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.Flow

class PhysicsQuestRepositoryImpl(private val db: PhysicsQuestDatabase) : PhysicsQuestRepository {

    private val worldBadgeIds = setOf(
        SeedBadges.EXPLORADOR_MOVIMIENTO, SeedBadges.MAESTRO_PALANCA,
        SeedBadges.INGENIERO_RAMPAS, SeedBadges.PILOTO_ENERGIA, SeedBadges.OIDO_DE_ORO
    )

    /**
     * Recalcula por completo las piezas de nave a partir de las insignias de
     * mundo que ya se tienen (una por mundo), en vez de ir sumando de a una:
     * así también se autocorrigen los perfiles que quedaron "atascados" en 0
     * por versiones anteriores de la app, apenas se vuelve a abrir o a jugar.
     */
    private suspend fun reconcileShipPieces(userProfileId: Long): Int {
        val earned = worldBadgeIds.count { badgeId -> db.userBadgeDao().hasBadge(userProfileId, badgeId) }
        db.userProfileDao().updateShipPieces(userProfileId, earned)
        return earned
    }

    override fun observeProfile(): Flow<UserProfile?> = db.userProfileDao().observeProfile()

    override suspend fun getOrCreateProfile(defaultName: String): UserProfile {
        db.userProfileDao().getProfileOnce()?.let { profile ->
            reconcileShipPieces(profile.id)
            return db.userProfileDao().getProfileOnce() ?: profile
        }
        val id = db.userProfileDao().insert(
            UserProfile(explorerName = defaultName, createdAtEpochMillis = System.currentTimeMillis())
        )
        return db.userProfileDao().getProfileOnce() ?: UserProfile(id = id, explorerName = defaultName, createdAtEpochMillis = System.currentTimeMillis())
    }

    override suspend fun renameProfile(profileId: Long, newName: String) {
        val profile = db.userProfileDao().getProfileOnce() ?: return
        if (profile.id == profileId) {
            db.userProfileDao().update(profile.copy(explorerName = newName))
        }
    }

    override fun observeWorlds(): Flow<List<World>> = db.worldDao().observeAll()
    override suspend fun getWorld(worldId: Long): World? = db.worldDao().getById(worldId)
    override fun observeLevelsForWorld(worldId: Long): Flow<List<Level>> = db.levelDao().observeLevelsForWorld(worldId)
    override fun observeAllLevels(): Flow<List<Level>> = db.levelDao().observeAll()
    override suspend fun getLevel(levelId: Long): Level? = db.levelDao().getLevel(levelId)
    override suspend fun getObjectsForLevel(levelId: Long): List<LevelObject> = db.levelDao().getObjectsForLevel(levelId)
    override suspend fun getRulesForLevel(levelId: Long): List<LevelRule> = db.levelDao().getRulesForLevel(levelId)
    override suspend fun getHintsForLevel(levelId: Long): List<Hint> = db.hintDao().getForLevel(levelId)

    override fun observeProgressForUser(userProfileId: Long): Flow<List<LevelProgress>> =
        db.levelProgressDao().observeAllForUser(userProfileId)

    override suspend fun getProgress(userProfileId: Long, levelId: Long): LevelProgress? =
        db.levelProgressDao().getForLevel(userProfileId, levelId)

    override suspend fun sumStarsForWorld(userProfileId: Long, worldId: Long): Int =
        db.levelProgressDao().sumStarsForWorld(userProfileId, worldId)

    override fun observeTotalStars(userProfileId: Long): Flow<Int> = db.levelProgressDao().observeTotalStars(userProfileId)

    override suspend fun countFailedAttempts(userProfileId: Long, levelId: Long): Int {
        val progress = db.levelProgressDao().getForLevel(userProfileId, levelId) ?: return 0
        return db.attemptDao().countFailuresForProgress(progress.id)
    }

    override suspend fun recordAttempt(
        userProfileId: Long,
        levelId: Long,
        result: PuzzleResult,
        attemptNumberInSession: Int,
        hintUsed: Boolean
    ): Attempt {
        var progress = db.levelProgressDao().getForLevel(userProfileId, levelId)
        if (progress == null) {
            val newId = db.levelProgressDao().insert(
                LevelProgress(userProfileId = userProfileId, levelId = levelId, unlocked = true)
            )
            progress = db.levelProgressDao().getForLevel(userProfileId, levelId)
                ?: LevelProgress(id = newId, userProfileId = userProfileId, levelId = levelId, unlocked = true)
        }

        // Cada nivel puede definir su propio número máximo de intentos para la
        // estrella de "pocos intentos" y su propio umbral de eficiencia; si no
        // los define, se usan los valores por defecto de StarCalculator.
        val rules = db.levelDao().getRulesForLevel(levelId)
        val maxAttemptsForStar = rules.firstOrNull { it.ruleType == com.kidslab.physicsquest.domain.model.LevelRuleType.INTENTOS_MAXIMOS_PARA_ESTRELLA }
            ?.value1?.toInt() ?: StarCalculator.DEFAULT_MAX_ATTEMPTS_FOR_STAR
        val efficiencyThreshold = rules.firstOrNull { it.ruleType == com.kidslab.physicsquest.domain.model.LevelRuleType.UMBRAL_EFICIENCIA }
            ?.value1 ?: StarCalculator.DEFAULT_EFFICIENCY_THRESHOLD

        val stars = StarCalculator.calculateStars(result, attemptNumberInSession, maxAttemptsForStar, efficiencyThreshold)

        val attempt = Attempt(
            levelProgressId = progress.id,
            attemptNumber = attemptNumberInSession,
            success = result.success,
            efficiencyScore = result.efficiencyScore,
            starsEarned = stars,
            hintUsed = hintUsed,
            timestampEpochMillis = System.currentTimeMillis()
        )
        db.attemptDao().insert(attempt)

        // Los fallos nunca penalizan de forma permanente: solo se guarda el mejor resultado histórico.
        if (result.success && stars >= progress.stars) {
            val updated = progress.copy(
                stars = maxOf(stars, progress.stars),
                completed = true,
                bestAttemptsToComplete = progress.bestAttemptsToComplete?.let { minOf(it, attemptNumberInSession) } ?: attemptNumberInSession,
                lastPlayedAtEpochMillis = System.currentTimeMillis()
            )
            db.levelProgressDao().update(updated)
            recalculateTotalStars(userProfileId)
            maybeAwardBadges(userProfileId, levelId)
        } else {
            db.levelProgressDao().update(progress.copy(lastPlayedAtEpochMillis = System.currentTimeMillis()))
        }

        return attempt
    }

    private suspend fun recalculateTotalStars(userProfileId: Long) {
        val all = db.levelProgressDao().getAllForUserOnce(userProfileId)
        val total = all.sumOf { it.stars }
        db.userProfileDao().updateTotalStars(userProfileId, total)
    }

    private suspend fun maybeAwardBadges(userProfileId: Long, completedLevelId: Long) {
        val level = db.levelDao().getLevel(completedLevelId) ?: return

        if (level.levelNumberInWorld == 1) {
            awardBadgeIfNeeded(userProfileId, SeedBadges.PRIMER_LANZAMIENTO)
        }

        // ¿Se completaron los 6 niveles de este mundo? Usamos una consulta puntual
        // (no el Flow) porque aquí necesitamos el valor actual de forma síncrona.
        val progressList = db.levelProgressDao().getAllForUserOnce(userProfileId)
        val levelsInWorldCompleted = progressList.count { p ->
            p.completed && db.levelDao().getLevel(p.levelId)?.worldId == level.worldId
        }
        if (levelsInWorldCompleted >= 6) {
            val worldBadgeId = when (level.worldId) {
                1L -> SeedBadges.EXPLORADOR_MOVIMIENTO
                2L -> SeedBadges.MAESTRO_PALANCA
                3L -> SeedBadges.INGENIERO_RAMPAS
                4L -> SeedBadges.PILOTO_ENERGIA
                5L -> SeedBadges.OIDO_DE_ORO
                else -> null
            }
            worldBadgeId?.let { awardBadgeIfNeeded(userProfileId, it) }
        }

        // ¿Se desbloquearon las 25 tarjetas de concepto? (una por nivel completado con concepto asociado)
        val distinctConceptCards = progressList.mapNotNull { p ->
            if (p.completed) db.levelDao().getLevel(p.levelId)?.conceptCardId else null
        }.distinct()
        if (distinctConceptCards.size >= 25) {
            awardBadgeIfNeeded(userProfileId, SeedBadges.COLECCIONISTA_CONCEPTOS)
        }

        // ¿Se ganaron las 5 insignias de mundo? -> insignia final (nave completa).
        val earnedWorldBadges = reconcileShipPieces(userProfileId)
        if (earnedWorldBadges >= worldBadgeIds.size) {
            awardBadgeIfNeeded(userProfileId, SeedBadges.HEROE_DE_FISICA_QUEST)
        }
    }

    override fun observeConceptCards(): Flow<List<ConceptCard>> = db.conceptCardDao().observeAll()
    override suspend fun getBossChallengeForWorld(worldId: Long): BossChallenge? = db.bossChallengeDao().getForWorld(worldId)
    override fun observeBadges(): Flow<List<Badge>> = db.badgeDao().observeAll()
    override fun observeUserBadges(userProfileId: Long): Flow<List<UserBadge>> = db.userBadgeDao().observeForUser(userProfileId)

    override suspend fun awardBadgeIfNeeded(userProfileId: Long, badgeId: Long) {
        if (!db.userBadgeDao().hasBadge(userProfileId, badgeId)) {
            db.userBadgeDao().insert(UserBadge(userProfileId = userProfileId, badgeId = badgeId, earnedAtEpochMillis = System.currentTimeMillis()))
        }
    }

    override suspend fun ensureSeeded() = SeedDataProvider.ensureSeeded(db)
}
