package com.saffron.cook.core.database.repository

import com.saffron.cook.core.database.dao.CookedRecipeDao
import com.saffron.cook.core.database.entity.CookedRecipeEntity
import kotlinx.coroutines.flow.Flow

class RoomCookedRecipesRepository(private val dao: CookedRecipeDao) : CookedRecipesRepository {

    override val allCookedFlow: Flow<List<CookedRecipeEntity>> = dao.observeAll()
    override val totalCountFlow: Flow<Int> = dao.observeTotalCount()

    override suspend fun recordCooked(
        recipeId: String,
        recipeName: String,
        recipeImage: String,
        recipeCategory: String,
    ) {
        val inserted = dao.insert(
            CookedRecipeEntity(
                recipeId = recipeId,
                recipeName = recipeName,
                recipeImage = recipeImage,
                recipeCategory = recipeCategory,
            ),
        )
        if (inserted == -1L) {
            dao.incrementAndTouch(recipeId, System.currentTimeMillis())
        }
    }
}
