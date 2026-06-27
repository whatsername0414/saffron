package com.saffron.cook.ui.cookedlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saffron.cook.core.database.repository.CookedRecipesRepository
import com.saffron.cook.core.database.repository.SavedRecipesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CookedListViewModel(
    private val cookedRepository: CookedRecipesRepository,
    private val savedRecipesRepository: SavedRecipesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CookedListUiState(isLoading = true))
    val uiState: StateFlow<CookedListUiState> = _uiState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("d MMM", Locale.getDefault())

    init {
        viewModelScope.launch {
            combine(
                cookedRepository.allCookedFlow,
                savedRecipesRepository.savedIdsFlow,
            ) { cooked, savedIds ->
                CookedListUiState(
                    isLoading = false,
                    totalCooked = cooked.sumOf { it.times },
                    items = cooked.map { c ->
                        CookedListItem(
                            recipeId = c.recipeId,
                            recipeName = c.recipeName,
                            recipeImage = c.recipeImage,
                            recipeCategory = c.recipeCategory,
                            timesLabel = if (c.times == 1) "Once" else "${c.times} times",
                            lastCookedLabel = "last " + dateFormatter.format(Date(c.lastCookedAt)),
                            isSaved = c.recipeId in savedIds,
                        )
                    },
                )
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }
}
