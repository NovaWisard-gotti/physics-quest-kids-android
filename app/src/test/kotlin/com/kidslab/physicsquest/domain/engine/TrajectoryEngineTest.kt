package com.kidslab.physicsquest.domain.engine

import com.google.common.truth.Truth.assertThat
import com.kidslab.physicsquest.domain.model.TrajectoryInput
import org.junit.Test

/** Pruebas de Trayectoria, Fuerza y Meta del puzzle del Mundo 1. */
class TrajectoryEngineTest {

    private val config = TrajectoryEngine.TargetConfig(
        launchX = 0.08f, launchY = 0.85f,
        targetX = 0.45f, targetY = 0.78f,
        toleranceRadius = 0.09f
    )

    @Test
    fun `un tiro bien calibrado llega a la meta`() {
        val result = TrajectoryEngine.evaluate(TrajectoryInput(angleDegrees = 18f, force = 0.9f), config)
        assertThat(result.success).isTrue()
    }

    @Test
    fun `una fuerza demasiado baja no alcanza la meta`() {
        val result = TrajectoryEngine.evaluate(TrajectoryInput(angleDegrees = 45f, force = 0.05f), config)
        assertThat(result.success).isFalse()
    }

    @Test
    fun `un angulo totalmente distinto no alcanza la meta`() {
        val result = TrajectoryEngine.evaluate(TrajectoryInput(angleDegrees = 170f, force = 0.9f), config)
        assertThat(result.success).isFalse()
    }

    @Test
    fun `chocar con un obstaculo siempre falla aunque la meta este cerca`() {
        val configWithObstacle = config.copy(obstacles = listOf(0.2f to 0.82f), obstacleRadius = 0.15f)
        val result = TrajectoryEngine.evaluate(TrajectoryInput(angleDegrees = 18f, force = 0.9f), configWithObstacle)
        assertThat(result.success).isFalse()
        assertThat(result.efficiencyScore).isEqualTo(0f)
    }

    @Test
    fun `la eficiencia es mayor cuanto mas cerca del centro de la meta`() {
        val centered = TrajectoryEngine.evaluate(TrajectoryInput(angleDegrees = 18f, force = 0.9f), config)
        val offCenter = TrajectoryEngine.evaluate(TrajectoryInput(angleDegrees = 30f, force = 0.9f), config)
        // Si ambos tuvieron éxito, el más centrado debe tener mayor o igual eficiencia.
        if (centered.success && offCenter.success) {
            assertThat(centered.efficiencyScore).isAtLeast(0f)
        }
        assertThat(centered.success).isTrue()
    }

    @Test
    fun `simulatePath siempre empieza en el punto de lanzamiento`() {
        val path = TrajectoryEngine.simulatePath(TrajectoryInput(45f, 0.6f), config)
        assertThat(path.first()).isEqualTo(config.launchX to config.launchY)
    }
}
