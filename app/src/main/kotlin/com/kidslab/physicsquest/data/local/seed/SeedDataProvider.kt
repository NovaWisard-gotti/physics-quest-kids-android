package com.kidslab.physicsquest.data.local.seed

import com.kidslab.physicsquest.data.local.PhysicsQuestDatabase

/**
 * Carga el contenido fijo del juego (mundos, niveles, tarjetas de
 * concepto, insignias y jefes científicos) la primera vez que se abre la
 * app. Es idempotente: si ya hay mundos guardados, no hace nada.
 */
object SeedDataProvider {
    suspend fun ensureSeeded(db: PhysicsQuestDatabase) {
        if (db.worldDao().count() > 0) return

        db.worldDao().insertAll(SeedWorlds.all)

        val allBundles = SeedLevelsMovimiento.bundles +
            SeedLevelsFuerzas.bundles +
            SeedLevelsMaquinasSimples.bundles +
            SeedLevelsEnergia.bundles +
            SeedLevelsSonido.bundles

        db.levelDao().insertLevels(allBundles.map { it.level })
        db.levelDao().insertObjects(allBundles.flatMap { it.objects })
        db.levelDao().insertRules(allBundles.flatMap { it.rules })
        db.hintDao().insertAll(allBundles.flatMap { it.hints })

        db.conceptCardDao().insertAll(SeedConceptCards.all)
        db.badgeDao().insertAll(SeedBadges.all)
        db.bossChallengeDao().insertAll(SeedBossChallenges.all)
    }
}
