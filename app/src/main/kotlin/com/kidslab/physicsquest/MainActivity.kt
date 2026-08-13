package com.kidslab.physicsquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.kidslab.physicsquest.ui.navigation.PhysicsQuestNavHost
import com.kidslab.physicsquest.ui.theme.PhysicsQuestTheme

/**
 * Única Activity de Física Quest (patrón single-activity con Jetpack
 * Compose Navigation). La app es 100% offline: no se realiza ninguna
 * llamada de red desde aquí ni desde ningún otro punto del código.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as PhysicsQuestApp).container.repository
        setContent {
            PhysicsQuestTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PhysicsQuestNavHost(repository = repository)
                }
            }
        }
    }
}
