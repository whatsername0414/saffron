package com.saffron.cook.core.testing

import com.saffron.cook.core.database.entity.CookedRecipeEntity
import com.saffron.cook.core.database.entity.toCookedEntity
import com.saffron.cook.core.database.repository.CookedRecipesRepository
import com.saffron.cook.core.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeCookedRecipesRepository : CookedRecipesRepository {
    private val cooked = MutableStateFlow<List<CookedRecipeEntity>>(emptyList())

    override val allCookedFlow: Flow<List<CookedRecipeEntity>> = cooked.asStateFlow()
    override val totalCountFlow: Flow<Int> = cooked.map { list -> list.sumOf { it.times } }

    override suspend fun recordCooked(recipe: Recipe) {
        cooked.update { list ->
            val existing = list.firstOrNull { it.recipeId == recipe.id }
            if (existing == null) {
                list + recipe.toCookedEntity()
            } else {
                list.map {
                    if (it.recipeId == recipe.id) it.copy(times = it.times + 1, lastCookedAt = System.currentTimeMillis()) else it
                }
            }
        }
    }
}
