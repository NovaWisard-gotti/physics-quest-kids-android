package com.kidslab.physicsquest.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.physicsquest.data.local.entity.UserProfile
import com.kidslab.physicsquest.domain.repository.PhysicsQuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            val profile = repository.getOrCreateProfile("Explorador/a")
            _uiState.value = ProfileUiState(loading = false, profile = profile, editingName = profile.explorerName)
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
