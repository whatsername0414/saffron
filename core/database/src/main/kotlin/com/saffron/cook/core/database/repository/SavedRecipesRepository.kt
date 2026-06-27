package com.saffron.cook.core.database.repository

import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.database.SavedRecipeDao
import com.saffron.cook.core.database.SavedRecipeEntity
import com.saffron.cook.core.database.toEntity
import com.saffron.cook.core.database.toRecipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavedRecipesRepository(private val dao: SavedRecipeDao) {

    val savedIdsFlow: Flow<Set<String>> = dao.observeIds().map { it.toSet() }
    val savedRecipesFlow: Flow<List<Recipe>> = dao.observeAll().map { it.map { entity -> entity.toRecipe() } }

    suspend fun toggle(recipe: Recipe) {
        if (dao.count(recipe.id) > 0) dao.deleteById(recipe.id)
        else dao.insert(recipe.toEntity())
    }
}
