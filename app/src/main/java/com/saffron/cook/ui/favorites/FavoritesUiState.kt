package com.saffron.cook.ui.favorites

import com.saffron.cook.core.data.model.Recipe

data class FavoritesUiState(
    val recipes: List<Recipe> = emptyList(),
)
