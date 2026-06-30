package com.saffron.cook.core.database.repository

import com.saffron.cook.core.database.dao.SavedRecipeDao
import com.saffron.cook.core.database.entity.toEntity
import com.saffron.cook.core.database.entity.toRecipe
import com.saffron.cook.core.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomSavedRecipesRepository(private val dao: SavedRecipeDao) : SavedRecipesRepository {

    override val savedIdsFlow: Flow<Set<String>> = dao.observeIds().map { it.toSet() }
    override val savedRecipesFlow: Flow<List<Recipe>> =
        dao.observeAll().map { it.map { entity -> entity.toRecipe() } }

    override suspend fun toggle(recipe: Recipe) {
        if (dao.count(recipe.id) > 0) dao.deleteById(recipe.id)
        else dao.insert(recipe.toEntity())
    }
}
