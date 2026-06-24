package com.saffron.cook.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saffron.cook.core.data.repository.RecipeRepository
import com.saffron.cook.data.SavedRecipesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: RecipeRepository,
    private val savedRecipesRepository: SavedRecipesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            savedRecipesRepository.savedIdsFlow.collect { ids ->
                _uiState.update { it.copy(savedIds = ids) }
            }
        }
        viewModelScope.launch { loadInitial() }
    }

    private suspend fun loadInitial() {
        runCatching { repository.getRecipes() }
            .onSuccess { recipes -> _uiState.update { it.copy(isLoading = false, initialRecipes = recipes) } }
            .onFailure { _uiState.update { it.copy(isLoading = false) } }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), isLoading = false, isError = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            _uiState.update { it.copy(isLoading = true, isError = false) }
            runCatching { repository.searchRecipes(query) }
                .onSuccess { results ->
                    _uiState.update { it.copy(isLoading = false, results = results) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                }
        }
    }

    fun onToggleSave(recipeId: String) {
        val recipe = (_uiState.value.results + _uiState.value.initialRecipes)
            .firstOrNull { it.id == recipeId } ?: return
        viewModelScope.launch { savedRecipesRepository.toggle(recipe) }
    }
}
