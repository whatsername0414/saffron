package com.saffron.cook.data

import com.saffron.cook.core.data.model.Recipe
import com.saffron.cook.data.local.SavedRecipeDao
import com.saffron.cook.data.local.SavedRecipeEntity
import com.saffron.cook.data.local.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavedRecipesRepository(private val dao: SavedRecipeDao) {

    val savedIdsFlow: Flow<Set<String>> = dao.observeIds().map { it.toSet() }
    val savedRecipesFlow: Flow<List<SavedRecipeEntity>> = dao.observeAll()

    suspend fun toggle(recipe: Recipe) {
        if (dao.count(recipe.id) > 0) dao.deleteById(recipe.id)
        else dao.insert(recipe.toEntity())
    }
}
