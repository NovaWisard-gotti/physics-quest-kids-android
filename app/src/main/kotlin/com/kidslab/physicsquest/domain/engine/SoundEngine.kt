package com.kidslab.physicsquest.domain.engine

import com.kidslab.physicsquest.domain.model.PuzzleResult
import com.kidslab.physicsquest.domain.model.SoundInput

/**
 * Motor del puzzle de Sonido (Mundo 5: Sonido y ondas).
 *
 * Relaciona dos ideas centrales, sin fórmulas:
 *  - Frecuencia -> grave (frecuencia baja) o agudo (frecuencia alta).
 *  - Amplitud -> suave (amplitud baja) o fuerte (amplitud alta).
 *
 * El jugador debe producir un sonido con frecuencia y amplitud dentro de
 * los rangos objetivo del nivel (por ejemplo: "un sonido grave y fuerte").
 */
object SoundEngine {

    data class TargetConfig(
        val minFrequencyHz: Float,
        val maxFrequencyHz: Float,
        val minAmplitude: Float,
        val maxAmplitude: Float
    )

    fun pitchLabel(frequencyHz: Float): String = if (frequencyHz < 500f) "grave" else "agudo"
    fun loudnessLabel(amplitude: Float): String = if (amplitude < 0.5f) "suave" else "fuerte"

    fun evaluate(input: SoundInput, config: TargetConfig): PuzzleResult {
        val freqOk = input.frequencyHz in config.minFrequencyHz..config.maxFrequencyHz
        val ampOk = input.amplitude in config.minAmplitude..config.maxAmplitude

        if (!freqOk || !ampOk) {
            val hint = when {
                !freqOk && !ampOk -> "Ajusta la frecuencia y la amplitud del sonido."
                !freqOk -> "La altura del sonido no es la correcta: prueba más ${if (input.frequencyHz < config.minFrequencyHz) "agudo" else "grave"}."
                else -> "El volumen no es el correcto: prueba más ${if (input.amplitude < config.minAmplitude) "fuerte" else "suave"}."
            }
            return PuzzleResult(false, 0f, hint)
        }

        val freqCenter = (config.minFrequencyHz + config.maxFrequencyHz) / 2f
        val freqRange = ((config.maxFrequencyHz - config.minFrequencyHz) / 2f).coerceAtLeast(1f)
        val freqCloseness = 1f - (kotlin.math.abs(input.frequencyHz - freqCenter) / freqRange).coerceIn(0f, 1f)

        val ampCenter = (config.minAmplitude + config.maxAmplitude) / 2f
        val ampRange = ((config.maxAmplitude - config.minAmplitude) / 2f).coerceAtLeast(0.01f)
        val ampCloseness = 1f - (kotlin.math.abs(input.amplitude - ampCenter) / ampRange).coerceIn(0f, 1f)

        val efficiency = ((freqCloseness + ampCloseness) / 2f).coerceIn(0f, 1f)
        val message = "¡Sonido ${pitchLabel(input.frequencyHz)} y ${loudnessLabel(input.amplitude)}, tal como pedía el nivel!"
        return PuzzleResult(true, efficiency, message)
    }
}
