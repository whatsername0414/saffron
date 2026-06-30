package com.saffron.cook.core.database.repository

import com.saffron.cook.core.database.entity.CookedRecipeEntity
import com.saffron.cook.core.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface CookedRecipesRepository {
    val allCookedFlow: Flow<List<CookedRecipeEntity>>
    val totalCountFlow: Flow<Int>

    suspend fun recordCooked(recipe: Recipe)
}
