package com.kidslab.physicsquest.domain.engine

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/** Pruebas de la política de Pistas: disponibles a partir del segundo intento fallido. */
class HintPolicyTest {

    @Test
    fun `sin intentos fallidos la pista no esta disponible`() {
        assertThat(HintPolicy.isHintAvailable(failedAttemptsSoFar = 0)).isFalse()
    }

    @Test
    fun `con un solo intento fallido la pista todavia no esta disponible`() {
        assertThat(HintPolicy.isHintAvailable(failedAttemptsSoFar = 1)).isFalse()
    }

    @Test
    fun `con dos intentos fallidos la pista se habilita`() {
        assertThat(HintPolicy.isHintAvailable(failedAttemptsSoFar = 2)).isTrue()
    }

    @Test
    fun `con mas de dos intentos fallidos la pista sigue disponible`() {
        assertThat(HintPolicy.isHintAvailable(failedAttemptsSoFar = 5)).isTrue()
    }
}
