package com.kidslab.physicsquest.domain.engine

import com.google.common.truth.Truth.assertThat
import com.kidslab.physicsquest.domain.model.SoundInput
import org.junit.Test

/** Pruebas de Sonido del puzzle del Mundo 5. */
class SoundEngineTest {

    private val config = SoundEngine.TargetConfig(
        minFrequencyHz = 100f, maxFrequencyHz = 300f, minAmplitude = 0.1f, maxAmplitude = 0.4f
    )

    @Test
    fun `frecuencia y amplitud dentro del rango son un exito`() {
        val result = SoundEngine.evaluate(SoundInput(frequencyHz = 200f, amplitude = 0.25f), config)
        assertThat(result.success).isTrue()
    }

    @Test
    fun `frecuencia fuera de rango falla aunque la amplitud sea correcta`() {
        val result = SoundEngine.evaluate(SoundInput(frequencyHz = 1500f, amplitude = 0.25f), config)
        assertThat(result.success).isFalse()
    }

    @Test
    fun `amplitud fuera de rango falla aunque la frecuencia sea correcta`() {
        val result = SoundEngine.evaluate(SoundInput(frequencyHz = 200f, amplitude = 0.9f), config)
        assertThat(result.success).isFalse()
    }

    @Test
    fun `pitchLabel clasifica correctamente grave y agudo`() {
        assertThat(SoundEngine.pitchLabel(200f)).isEqualTo("grave")
        assertThat(SoundEngine.pitchLabel(1000f)).isEqualTo("agudo")
    }

    @Test
    fun `loudnessLabel clasifica correctamente suave y fuerte`() {
        assertThat(SoundEngine.loudnessLabel(0.2f)).isEqualTo("suave")
        assertThat(SoundEngine.loudnessLabel(0.8f)).isEqualTo("fuerte")
    }
}
