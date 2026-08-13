package com.kidslab.physicsquest.domain.engine

import com.google.common.truth.Truth.assertThat
import com.kidslab.physicsquest.domain.model.InclinedPlaneInput
import org.junit.Test

/** Pruebas de Plano Inclinado del puzzle del Mundo 3. */
class InclinedPlaneEngineTest {

    private val short = InclinedPlaneEngine.RampOption(objectId = 1L, length = 0.9f, height = 1.0f)
    private val medium = InclinedPlaneEngine.RampOption(objectId = 2L, length = 1.6f, height = 1.0f)
    private val long = InclinedPlaneEngine.RampOption(objectId = 3L, length = 2.4f, height = 1.0f)
    private val config = InclinedPlaneEngine.TargetConfig(
        loadWeight = 0.5f, maxAvailableEffort = 0.45f, ramps = listOf(short, medium, long)
    )

    @Test
    fun `la rampa corta y empinada requiere demasiado esfuerzo y falla`() {
        val result = InclinedPlaneEngine.evaluate(InclinedPlaneInput(short.objectId), config)
        assertThat(result.success).isFalse()
    }

    @Test
    fun `la rampa larga es mas eficiente que la media`() {
        val mediumResult = InclinedPlaneEngine.evaluate(InclinedPlaneInput(medium.objectId), config)
        val longResult = InclinedPlaneEngine.evaluate(InclinedPlaneInput(long.objectId), config)
        assertThat(mediumResult.success).isTrue()
        assertThat(longResult.success).isTrue()
        assertThat(longResult.efficiencyScore).isAtLeast(mediumResult.efficiencyScore)
    }

    @Test
    fun `elegir una rampa que no existe en el nivel no produce exito`() {
        val result = InclinedPlaneEngine.evaluate(InclinedPlaneInput(999L), config)
        assertThat(result.success).isFalse()
    }
}
