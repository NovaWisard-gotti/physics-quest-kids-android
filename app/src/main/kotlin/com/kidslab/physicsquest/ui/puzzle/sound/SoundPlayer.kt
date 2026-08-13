package com.kidslab.physicsquest.ui.puzzle.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.sin

/**
 * Genera un tono puro (onda senoidal) 100% en el dispositivo, sin
 * archivos de audio ni conexión a internet. Se usa en el puzzle de
 * Sonido para que el niño o niña "escuche" la frecuencia y la amplitud
 * que eligió.
 */
class SoundPlayer {
    private val sampleRate = 44100
    private var track: AudioTrack? = null

    fun playTone(frequencyHz: Float, amplitude: Float, durationSeconds: Float = 0.8f) {
        stop()
        val numSamples = (sampleRate * durationSeconds).toInt()
        val buffer = ShortArray(numSamples)
        val safeAmplitude = amplitude.coerceIn(0f, 1f)
        for (i in buffer.indices) {
            val angle = 2.0 * Math.PI * i * frequencyHz / sampleRate
            // Pequeño fundido de entrada/salida para evitar "clics" al iniciar y terminar el sonido.
            val fade = minOf(i / 400f, (buffer.size - i) / 400f, 1f).coerceIn(0f, 1f)
            buffer[i] = (sin(angle) * safeAmplitude * fade * Short.MAX_VALUE).toInt().toShort()
        }

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        track = audioTrack
    }

    fun stop() {
        track?.let {
            runCatching { it.stop() }
            it.release()
        }
        track = null
    }
}
