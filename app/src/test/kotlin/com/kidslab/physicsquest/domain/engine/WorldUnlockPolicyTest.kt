package com.kidslab.physicsquest.domain.engine

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/** Pruebas de Desbloqueo de mundos. */
class WorldUnlockPolicyTest {

    @Test
    fun `el primer mundo siempre esta desbloqueado`() {
        assertThat(WorldUnlockPolicy.isUnlocked(worldOrder = 1, starsRequiredToUnlock = 0, starsInPreviousWorld = 0)).isTrue()
    }

    @Test
    fun `un mundo permanece bloqueado si no hay suficientes estrellas en el mundo anterior`() {
        assertThat(WorldUnlockPolicy.isUnlocked(worldOrder = 2, starsRequiredToUnlock = 12, starsInPreviousWorld = 5)).isFalse()
    }

    @Test
    fun `un mundo se desbloquea al alcanzar el minimo de estrellas`() {
        assertThat(WorldUnlockPolicy.isUnlocked(worldOrder = 2, starsRequiredToUnlock = 12, starsInPreviousWorld = 12)).isTrue()
    }

    @Test
    fun `un mundo se desbloquea con mas estrellas de las necesarias`() {
        assertThat(WorldUnlockPolicy.isUnlocked(worldOrder = 3, starsRequiredToUnlock = 12, starsInPreviousWorld = 18)).isTrue()
    }

    @Test
    fun `el desbloqueo de un jefe cientifico sigue la misma regla dentro de su mundo`() {
        assertThat(WorldUnlockPolicy.isBossUnlocked(starsRequiredToUnlock = 9, starsInThisWorld = 8)).isFalse()
        assertThat(WorldUnlockPolicy.isBossUnlocked(starsRequiredToUnlock = 9, starsInThisWorld = 9)).isTrue()
    }
}
