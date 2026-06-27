package com.saffron.cook.ui.search

import com.saffron.cook.core.domain.model.Recipe

data class SearchUiState(
    val query: String = "",
    val results: List<Recipe> = emptyList(),
    val initialRecipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val savedIds: Set<String> = emptySet(),
)
