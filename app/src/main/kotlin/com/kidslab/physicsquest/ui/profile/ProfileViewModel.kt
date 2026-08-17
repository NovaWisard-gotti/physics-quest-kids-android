package com.kidslab.physicsquest.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.UserProfile
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Nombre "de fábrica" del perfil: nunca se le muestra al niño como texto ya
 * escrito, sólo como sugerencia (placeholder) en el campo mientras esté vacío. */
const val DEFAULT_EXPLORER_NAME = "Explorador/a"

data class ProfileUiState(
    val loading: Boolean = true,
    val profile: UserProfile? = null,
    val editingName: String = ""
)

class ProfileViewModel(private val repository: PhysicsQuestRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            val profile = repository.getOrCreateProfile(DEFAULT_EXPLORER_NAME)
            // Si el niño todavía no eligió un nombre propio, el campo arranca
            // vacío y "Explorador/a" se muestra solo como marca de agua.
            val startingName = if (profile.explorerName == DEFAULT_EXPLORER_NAME) "" else profile.explorerName
            _uiState.value = ProfileUiState(loading = false, profile = profile, editingName = startingName)
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(editingName = name)
    }

    fun saveName() {
        val state = _uiState.value
        val profile = state.profile ?: return
        if (state.editingName.isBlank()) return
        viewModelScope.launch {
            repository.renameProfile(profile.id, state.editingName.trim())
            _uiState.value = state.copy(profile = profile.copy(explorerName = state.editingName.trim()))
        }
    }

    class Factory(private val repository: PhysicsQuestRepository) : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T = ProfileViewModel(repository) as T
    }
}
