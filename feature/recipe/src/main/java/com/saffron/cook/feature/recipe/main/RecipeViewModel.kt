package com.saffron.cook.feature.recipe.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saffron.cook.core.domain.repository.RecipeRepository
import com.saffron.cook.core.database.repository.SavedRecipesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: RecipeRepository,
    private val savedRecipesRepository: SavedRecipesRepository,
) : ViewModel() {

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"])

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            savedRecipesRepository.savedIdsFlow.collect { ids ->
                _uiState.update { it.copy(savedIds = ids, isSaved = it.recipe?.id in ids) }
            }
        }
        viewModelScope.launch { load() }
    }

    private suspend fun load() {
        _uiState.update { it.copy(isLoading = true, isError = false) }
        try {
            val recipe = repository.getRecipeById(recipeId)
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    recipe = recipe,
                    isSaved = recipe?.id in state.savedIds,
                )
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(isLoading = false, isError = true) }
        }
    }

    fun retry() {
        viewModelScope.launch { load() }
    }

    fun onToggleSave() {
        val recipe = _uiState.value.recipe ?: return
        viewModelScope.launch { savedRecipesRepository.toggle(recipe) }
    }
}
