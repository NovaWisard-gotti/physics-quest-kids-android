package com.kidslab.physicsquest

import android.app.Application
import com.kidslab.physicsquest.di.AppContainer

/** Application de Física Quest. Aplicación 100% offline, sin permisos ni servicios externos. */
class PhysicsQuestApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
