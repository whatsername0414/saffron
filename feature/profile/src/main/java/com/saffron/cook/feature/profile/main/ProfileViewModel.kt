package com.saffron.cook.feature.profile.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saffron.cook.core.auth.AuthRepository
import com.saffron.cook.core.database.repository.CookedRecipesRepository
import com.saffron.cook.core.database.repository.RecipeNotesRepository
import com.saffron.cook.core.database.repository.SavedRecipesRepository
import com.saffron.cook.core.database.repository.ThemeMode
import com.saffron.cook.core.database.repository.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val savedRecipesRepository: SavedRecipesRepository,
    private val notesRepository: RecipeNotesRepository,
    private val cookedRepository: CookedRecipesRepository,
    private val themeRepository: ThemeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(user = authRepository.currentUserSnapshot)
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
        viewModelScope.launch {
            savedRecipesRepository.savedIdsFlow.collect { ids ->
                _uiState.update { it.copy(savedCount = ids.size) }
            }
        }
        viewModelScope.launch {
            notesRepository.noteCountFlow.collect { count ->
                _uiState.update { it.copy(notesCount = count) }
            }
        }
        viewModelScope.launch {
            cookedRepository.totalCountFlow.collect { count ->
                _uiState.update { it.copy(cookedCount = count) }
            }
        }
        viewModelScope.launch {
            themeRepository.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) = themeRepository.setThemeMode(mode)

    fun handleGoogleIdToken(idToken: String) {
        _uiState.update { it.copy(isSigningIn = true) }
        viewModelScope.launch {
            authRepository.signInWithGoogle(idToken)
                .onSuccess { _uiState.update { it.copy(isSigningIn = false) } }
                .onFailure { _uiState.update { it.copy(isSigningIn = false) } }
        }
    }

    fun signOut() = authRepository.signOut()
}
