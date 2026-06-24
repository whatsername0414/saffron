package com.saffron.cook.ui.favorites

import com.saffron.cook.data.local.SavedRecipeEntity

data class FavoritesUiState(
    val savedRecipes: List<SavedRecipeEntity> = emptyList(),
)
