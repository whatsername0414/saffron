package com.saffron.cook.ui.cooking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saffron.cook.core.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CookingModeViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: RecipeRepository,
) : ViewModel() {

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"])

    private val _uiState = MutableStateFlow(CookingModeUiState())
    val uiState: StateFlow<CookingModeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { load() }
    }

    private suspend fun load() {
        val recipe = repository.getRecipeById(recipeId)
        _uiState.update { it.copy(isLoading = false, recipe = recipe) }
    }

    fun onSelectStep(index: Int) {
        _uiState.update { it.copy(currentStepIndex = index) }
    }

    fun onToggleStepDone() {
        _uiState.update { state ->
            val completed = state.completedSteps.toMutableSet()
            if (state.currentStepIndex in completed) completed.remove(state.currentStepIndex)
            else completed.add(state.currentStepIndex)
            state.copy(completedSteps = completed)
        }
    }

    fun onNext() {
        _uiState.update { state ->
            if (!state.isLastStep) state.copy(currentStepIndex = state.currentStepIndex + 1)
            else state
        }
    }

    fun onPrevious() {
        _uiState.update { state ->
            if (!state.isFirstStep) state.copy(currentStepIndex = state.currentStepIndex - 1)
            else state
        }
    }
}
