package com.saffron.cook.ui.home

import com.saffron.cook.core.data.model.Category
import com.saffron.cook.core.data.model.Recipe

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
