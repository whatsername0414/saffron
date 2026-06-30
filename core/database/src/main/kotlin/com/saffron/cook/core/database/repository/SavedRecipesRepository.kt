package com.saffron.cook.core.database.repository

import com.saffron.cook.core.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface SavedRecipesRepository {
    val savedIdsFlow: Flow<Set<String>>
    val savedRecipesFlow: Flow<List<Recipe>>
    suspend fun toggle(recipe: Recipe)
}
