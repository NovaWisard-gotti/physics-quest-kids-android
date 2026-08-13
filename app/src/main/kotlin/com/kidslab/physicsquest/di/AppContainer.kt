package com.kidslab.physicsquest.di

import android.content.Context
import com.kidslab.physicsquest.data.local.PhysicsQuestDatabase
import com.kidslab.physicsquest.data.repository.PhysicsQuestRepositoryImpl
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository

/**
 * Contenedor manual de dependencias (sin frameworks externos como Hilt).
 * Se crea una sola vez en [com.kidslab.physicsquest.PhysicsQuestApp] y se
 * pasa a los ViewModels a través de una Factory sencilla.
 */
class AppContainer(context: Context) {
    private val database: PhysicsQuestDatabase = PhysicsQuestDatabase.getInstance(context)

    val repository: PhysicsQuestRepository by lazy { PhysicsQuestRepositoryImpl(database) }
}
