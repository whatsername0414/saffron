package com.saffron.cook.feature.home.main

import com.saffron.cook.core.domain.model.Category
import com.saffron.cook.core.domain.model.Recipe

data class HomeUiState(
    val isLoading: Boolean = true,
    val greeting: String = "",
    val dateLabel: String = "",
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,
    val featuredRecipe: Recipe? = null,
    val recipes: List<Recipe> = emptyList(),
    val savedIds: Set<String> = emptySet(),
)
