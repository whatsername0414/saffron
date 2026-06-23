package com.saffron.cook.ui.detail

import com.saffron.cook.core.data.model.Recipe

data class RecipeDetailUiState(
    val isLoading: Boolean = true,
    val recipe: Recipe? = null,
    val isSaved: Boolean = false,
    val isError: Boolean = false,
)
