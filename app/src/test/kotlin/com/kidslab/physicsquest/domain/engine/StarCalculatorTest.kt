package com.kidslab.physicsquest.domain.engine

import com.google.common.truth.Truth.assertThat
import com.kidslab.physicsquest.domain.model.PuzzleResult
import org.junit.Test

/** Pruebas de la regla de Estrellas: 1 por completar, 1 por pocos intentos, 1 por eficiencia. */
class StarCalculatorTest {

    @Test
    fun `un fallo nunca otorga estrellas`() {
        val result = PuzzleResult(success = false, efficiencyScore = 0.9f, feedbackMessage = "")
        val stars = StarCalculator.calculateStars(result, attemptsUsedIncludingThisOne = 1)
        assertThat(stars).isEqualTo(0)
    }

    @Test
    fun `completar en muchos intentos con baja eficiencia da solo 1 estrella`() {
        val result = PuzzleResult(success = true, efficiencyScore = 0.1f, feedbackMessage = "")
        val stars = StarCalculator.calculateStars(result, attemptsUsedIncludingThisOne = 5)
        assertThat(stars).isEqualTo(1)
    }

    @Test
    fun `completar en pocos intentos con baja eficiencia da 2 estrellas`() {
        val result = PuzzleResult(success = true, efficiencyScore = 0.1f, feedbackMessage = "")
        val stars = StarCalculator.calculateStars(result, attemptsUsedIncludingThisOne = 1)
        assertThat(stars).isEqualTo(2)
    }

    @Test
    fun `completar en pocos intentos con alta eficiencia da 3 estrellas`() {
        val result = PuzzleResult(success = true, efficiencyScore = 0.95f, feedbackMessage = "")
        val stars = StarCalculator.calculateStars(result, attemptsUsedIncludingThisOne = 1)
        assertThat(stars).isEqualTo(3)
    }

    @Test
    fun `muchos intentos pero alta eficiencia da 2 estrellas`() {
        val result = PuzzleResult(success = true, efficiencyScore = 0.95f, feedbackMessage = "")
        val stars = StarCalculator.calculateStars(result, attemptsUsedIncludingThisOne = 6)
        assertThat(stars).isEqualTo(2)
    }
}
