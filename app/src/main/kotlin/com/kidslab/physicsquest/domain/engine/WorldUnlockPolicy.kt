package com.kidslab.physicsquest.domain.engine

/**
 * Regla de desbloqueo de mundos: cada mundo define cuántas estrellas se
 * necesitan acumuladas en el mundo ANTERIOR para poder entrar en él. El
 * primer mundo (Movimiento) siempre está desbloqueado.
 */
object WorldUnlockPolicy {
    fun isUnlocked(worldOrder: Int, starsRequiredToUnlock: Int, starsInPreviousWorld: Int): Boolean {
        if (worldOrder <= 1) return true
        return starsInPreviousWorld >= starsRequiredToUnlock
    }

    /** Un jefe científico se desbloquea al reunir X estrellas dentro de su propio mundo. */
    fun isBossUnlocked(starsRequiredToUnlock: Int, starsInThisWorld: Int): Boolean =
        starsInThisWorld >= starsRequiredToUnlock
}
