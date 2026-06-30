package com.saffron.cook.core.database.repository

import com.saffron.cook.core.database.dao.CookedRecipeDao
import com.saffron.cook.core.database.entity.CookedRecipeEntity
import com.saffron.cook.core.database.entity.toCookedEntity
import com.saffron.cook.core.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

class CookedRecipesRepositoryImpl(private val dao: CookedRecipeDao) : CookedRecipesRepository {

    override val allCookedFlow: Flow<List<CookedRecipeEntity>> = dao.observeAll()
    override val totalCountFlow: Flow<Int> = dao.observeTotalCount()

    override suspend fun recordCooked(recipe: Recipe) {
        val inserted = dao.insert(recipe.toCookedEntity())
        if (inserted == -1L) {
            dao.incrementAndTouch(recipe.id, System.currentTimeMillis())
        }
    }
}
