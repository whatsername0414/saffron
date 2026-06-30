package com.saffron.cook.feature.recipe.main

import com.saffron.cook.core.domain.model.Recipe

data class RecipeUiState(
    val isLoading: Boolean = true,
    val recipe: Recipe? = null,
    val isSaved: Boolean = false,
    val isError: Boolean = false,
    val savedIds: Set<String> = emptySet(),
)
