package com.kidslab.physicsquest.domain.engine

import com.google.common.truth.Truth.assertThat
import com.kidslab.physicsquest.domain.model.LeverInput
import org.junit.Test

/** Pruebas de Palanca del puzzle del Mundo 2. */
class LeverEngineTest {

    private val config = LeverEngine.TargetConfig(loadWeight = 0.5f)

    @Test
    fun `apoyo cerca de la carga con fuerza suficiente levanta el objeto`() {
        val result = LeverEngine.evaluate(LeverInput(fulcrumPosition = 0.2f, effortForce = 0.5f), config)
        assertThat(result.success).isTrue()
    }

    @Test
    fun `fuerza insuficiente para el punto de apoyo elegido falla`() {
        val result = LeverEngine.evaluate(LeverInput(fulcrumPosition = 0.5f, effortForce = 0.05f), config)
        assertThat(result.success).isFalse()
    }

    @Test
    fun `acercar el apoyo a la carga reduce la fuerza minima necesaria`() {
        val far = LeverEngine.evaluate(LeverInput(fulcrumPosition = 0.5f, effortForce = 1f), config)
        val near = LeverEngine.evaluate(LeverInput(fulcrumPosition = 0.1f, effortForce = 1f), config)
        assertThat(far.success).isTrue()
        assertThat(near.success).isTrue()
        // Con el apoyo más cerca de la carga, usar la fuerza máxima "sobra" más -> menor eficiencia por exceso.
        assertThat(near.efficiencyScore).isAtMost(far.efficiencyScore + 0.01f)
    }

    @Test
    fun `un punto de apoyo pegado a un extremo se rechaza como invalido`() {
        val result = LeverEngine.evaluate(LeverInput(fulcrumPosition = 0f, effortForce = 1f), config)
        assertThat(result.success).isFalse()
    }
}
