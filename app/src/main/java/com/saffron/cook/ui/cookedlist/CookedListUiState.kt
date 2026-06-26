package com.saffron.cook.ui.cookedlist

import androidx.compose.runtime.Stable

data class CookedListItem(
    val recipeId: String,
    val recipeName: String,
    val recipeImage: String,
    val recipeCategory: String,
    val timesLabel: String,
    val lastCookedLabel: String,
    val isSaved: Boolean,
)

@Stable
data class CookedListUiState(
    val items: List<CookedListItem> = emptyList(),
    val totalCooked: Int = 0,
    val isLoading: Boolean = false,
)
