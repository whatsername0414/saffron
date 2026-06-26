package com.saffron.cook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saffron.cook.core.data.repository.RecipeRepository
import com.saffron.cook.data.SavedRecipesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class HomeViewModel(
    private val repository: RecipeRepository,
    private val savedRecipesRepository: SavedRecipesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(buildShellState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var categoryJob: Job? = null

    init {
        viewModelScope.launch {
            savedRecipesRepository.savedIdsFlow.collect { ids ->
                _uiState.update { it.copy(savedIds = ids) }
            }
        }
        viewModelScope.launch { loadData() }
    }

    private suspend fun loadData() {
        runCatching {
            coroutineScope {
                val featuredDeferred   = async { repository.getFeaturedRecipe() }
                val categoriesDeferred = async { repository.getCategories() }
                val recipesDeferred    = async { repository.getRecipes() }

                val featured   = featuredDeferred.await()
                val categories = categoriesDeferred.await()
                val grid       = recipesDeferred.await().filter { it.id != featured?.id }

                _uiState.update { state ->
                    state.copy(
                        isLoading      = false,
                        categories     = categories,
                        featuredRecipe = featured,
                        recipes    = grid,
                    )
                }
            }
        }.onFailure {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onToggleSave(recipeId: String) {
        val recipe = _uiState.value.recipes.find { it.id == recipeId }
            ?: _uiState.value.featuredRecipe?.takeIf { it.id == recipeId }
            ?: return
        viewModelScope.launch { savedRecipesRepository.toggle(recipe) }
    }

    fun onSelectCategory(categoryId: String) {
        val newId = if (_uiState.value.selectedCategoryId == categoryId) null else categoryId
        _uiState.update { it.copy(selectedCategoryId = newId, isLoading = true) }
        categoryJob?.cancel()
        categoryJob = viewModelScope.launch {
            val recipes = if (newId == null) repository.getRecipes()
                       else repository.getRecipesByCategory(newId)
            _uiState.update { it.copy(isLoading = false, recipes = recipes) }
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
