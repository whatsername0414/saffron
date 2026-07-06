package com.saffron.cook.core.database.repository

import com.saffron.cook.core.database.dao.SavedRecipeDao
import com.saffron.cook.core.database.entity.toEntity
import com.saffron.cook.core.database.entity.toRecipe
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavedRecipesRepositoryImpl(
    private val dao: SavedRecipeDao,
    private val recipeRepository: RecipeRepository,
) : SavedRecipesRepository {

    override val savedIdsFlow: Flow<Set<String>> = dao.observeIds().map { it.toSet() }
    override val savedRecipesFlow: Flow<List<Recipe>> =
        dao.observeAll().map { it.map { entity -> entity.toRecipe() } }

    override suspend fun toggle(recipe: Recipe) {
        if (dao.count(recipe.id) > 0) {
            dao.deleteById(recipe.id)
        } else {
            // List rows are partial (no ingredients/steps) — fetch the full recipe so
            // saved entries stay readable offline; fall back to the partial one if that fails.
            val toSave = if (recipe.steps.isEmpty()) {
                runCatching { recipeRepository.getRecipeById(recipe.id) }.getOrNull() ?: recipe
            } else {
                recipe
            }
            dao.insert(toSave.toEntity())
        }
    }
}
