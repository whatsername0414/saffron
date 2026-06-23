package com.saffron.cook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saffron.cook.core.data.repository.RecipeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class HomeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(buildShellState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadData() }
    }

    private suspend fun loadData() {
        val featuredDeferred  = viewModelScope.async { repository.getFeaturedRecipe() }
        val categoriesDeferred = viewModelScope.async { repository.getCategories() }
        val recipesDeferred   = viewModelScope.async { repository.getRecipes() }

        val featured   = featuredDeferred.await()
        val categories = categoriesDeferred.await()
        val grid       = recipesDeferred.await().filter { it.id != featured?.id }

        _uiState.update { state ->
            state.copy(
                isLoading      = false,
                categories     = categories,
                featuredRecipe = featured,
                gridRecipes    = grid,
            )
        }
    }

    fun onToggleSave(recipeId: String) {
        _uiState.update { state ->
            val saved = state.savedIds.toMutableSet()
            if (recipeId in saved) saved.remove(recipeId) else saved.add(recipeId)
            state.copy(savedIds = saved)
        }
    }

    fun onSelectCategory(categoryId: String) {
        val newId = if (_uiState.value.selectedCategoryId == categoryId) null else categoryId
        _uiState.update { it.copy(selectedCategoryId = newId, isLoading = true) }
        viewModelScope.launch {
            val grid = if (newId == null) repository.getRecipes()
                       else repository.getRecipesByCategory(newId)
            _uiState.update { it.copy(isLoading = false, gridRecipes = grid) }
        }
    }

    private fun buildShellState(): HomeUiState {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good morning."
            hour < 17 -> "Good afternoon."
            else      -> "Good evening."
        }
        val day       = cal.get(Calendar.DAY_OF_MONTH)
        val dayName   = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).orEmpty()
        val monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).orEmpty()
        return HomeUiState(greeting = greeting, dateLabel = "$dayName, $day $monthName")
    }
}
