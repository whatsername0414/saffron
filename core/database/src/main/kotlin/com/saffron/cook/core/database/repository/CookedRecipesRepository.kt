package com.saffron.cook.core.database.repository

import com.saffron.cook.core.database.CookedRecipeDao
import com.saffron.cook.core.database.CookedRecipeEntity
import kotlinx.coroutines.flow.Flow

class CookedRecipesRepository(private val dao: CookedRecipeDao) {

    val allCookedFlow: Flow<List<CookedRecipeEntity>> = dao.observeAll()
    val totalCountFlow: Flow<Int> = dao.observeTotalCount()

    suspend fun recordCooked(
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
