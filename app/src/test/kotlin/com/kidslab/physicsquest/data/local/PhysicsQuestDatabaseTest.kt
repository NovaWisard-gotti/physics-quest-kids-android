package com.kidslab.physicsquest.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.kidslab.physicsquest.data.local.entity.LevelProgress
import com.kidslab.physicsquest.data.local.entity.UserProfile
import com.kidslab.physicsquest.data.local.seed.SeedDataProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Pruebas de Persistencia: usan una base de datos Room en memoria (vía
 * Robolectric) para comprobar que el contenido semilla y el progreso del
 * jugador se guardan y se leen correctamente, tal como ocurrirá en el
 * dispositivo real.
 */
@RunWith(RobolectricTestRunner::class)
class PhysicsQuestDatabaseTest {

    private lateinit var db: PhysicsQuestDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PhysicsQuestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `sembrar la base de datos crea 5 mundos y 30 niveles`() = runBlocking {
        SeedDataProvider.ensureSeeded(db)

        assertThat(db.worldDao().count()).isEqualTo(5)
        assertThat(db.levelDao().count()).isEqualTo(30)
        assertThat(db.conceptCardDao().count()).isEqualTo(25)
        assertThat(db.badgeDao().count()).isEqualTo(8)
        assertThat(db.bossChallengeDao().count()).isEqualTo(5)
    }

    @Test
    fun `sembrar dos veces no duplica el contenido`() = runBlocking {
        SeedDataProvider.ensureSeeded(db)
        SeedDataProvider.ensureSeeded(db)

        assertThat(db.worldDao().count()).isEqualTo(5)
        assertThat(db.levelDao().count()).isEqualTo(30)
    }

    @Test
    fun `crear y leer el perfil del explorador persiste correctamente`() = runBlocking {
        val id = db.userProfileDao().insert(UserProfile(explorerName = "Ada", createdAtEpochMillis = 1000L))
        val loaded = db.userProfileDao().getProfileOnce()

        assertThat(loaded).isNotNull()
        assertThat(loaded!!.id).isEqualTo(id)
        assertThat(loaded.explorerName).isEqualTo("Ada")
    }

    @Test
    fun `el progreso de un nivel se guarda y se actualiza sin perder el mejor resultado`() = runBlocking {
        SeedDataProvider.ensureSeeded(db)
        val profileId = db.userProfileDao().insert(UserProfile(explorerName = "Ada", createdAtEpochMillis = 1000L))
        val levelId = 101L // Primer nivel del Mundo 1

        val progressId = db.levelProgressDao().insert(
            LevelProgress(userProfileId = profileId, levelId = levelId, stars = 2, completed = true, unlocked = true)
        )
        val loaded = db.levelProgressDao().getForLevel(profileId, levelId)
        assertThat(loaded).isNotNull()
        assertThat(loaded!!.stars).isEqualTo(2)

        // Actualizamos con un mejor resultado (3 estrellas) y comprobamos que persiste.
        db.levelProgressDao().update(loaded.copy(stars = 3))
        val updated = db.levelProgressDao().getForLevel(profileId, levelId)
        assertThat(updated!!.stars).isEqualTo(3)
        assertThat(progressId).isGreaterThan(0L)
    }

    @Test
    fun `las estrellas totales por mundo se calculan correctamente`() = runBlocking {
        SeedDataProvider.ensureSeeded(db)
        val profileId = db.userProfileDao().insert(UserProfile(explorerName = "Ada", createdAtEpochMillis = 1000L))

        db.levelProgressDao().insert(LevelProgress(userProfileId = profileId, levelId = 101L, stars = 3, completed = true, unlocked = true))
        db.levelProgressDao().insert(LevelProgress(userProfileId = profileId, levelId = 102L, stars = 2, completed = true, unlocked = true))

        val total = db.levelProgressDao().sumStarsForWorld(profileId, worldId = 1L)
        assertThat(total).isEqualTo(5)
    }
}
