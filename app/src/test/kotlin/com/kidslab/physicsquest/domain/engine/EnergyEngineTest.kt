package com.kidslab.physicsquest.domain.engine

import com.google.common.truth.Truth.assertThat
import com.kidslab.physicsquest.domain.model.EnergyInput
import com.kidslab.physicsquest.domain.model.EnergyTrackSegment
import org.junit.Test

/** Pruebas de Energía del puzzle del Mundo 4. */
class EnergyEngineTest {

    private val config = EnergyEngine.TargetConfig(startHeight = 1.0f, goalHeight = 0.05f, minArrivalSpeed = 0.9f)

    @Test
    fun `un recorrido descendente valido llega a la meta con velocidad suficiente`() {
        val input = EnergyInput(listOf(EnergyTrackSegment(0.6f), EnergyTrackSegment(0.3f)))
        val result = EnergyEngine.evaluate(input, config)
        assertThat(result.success).isTrue()
    }

    @Test
    fun `un tramo mas alto que la energia disponible hace fallar el recorrido`() {
        val input = EnergyInput(listOf(EnergyTrackSegment(1.5f)))
        val result = EnergyEngine.evaluate(input, config)
        assertThat(result.success).isFalse()
    }

    @Test
    fun `un recorrido vacio no es valido`() {
        val result = EnergyEngine.evaluate(EnergyInput(emptyList()), config)
        assertThat(result.success).isFalse()
    }

    @Test
    fun `llegar demasiado lento a la meta no cuenta como exito`() {
        val demandingConfig = config.copy(minArrivalSpeed = 5f)
        val input = EnergyInput(listOf(EnergyTrackSegment(0.6f), EnergyTrackSegment(0.3f)))
        val result = EnergyEngine.evaluate(input, demandingConfig)
        assertThat(result.success).isFalse()
    }
}
