package com.saffron.cook.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saffron.cook.data.SavedRecipesRepository
import com.saffron.cook.data.local.toRecipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(private val savedRecipesRepository: SavedRecipesRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            savedRecipesRepository.savedRecipesFlow.collect { recipes ->
                _uiState.update { it.copy(savedRecipes = recipes) }
            }
        }
    }

    fun onToggleSave(recipeId: String) {
        val entity = _uiState.value.savedRecipes.find { it.id == recipeId } ?: return
        viewModelScope.launch { savedRecipesRepository.toggle(entity.toRecipe()) }
    }
}
