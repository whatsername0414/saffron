package com.saffron.cook.core.database.repository

import com.saffron.cook.core.database.dao.CachedRecipeDao
import com.saffron.cook.core.database.dao.SavedRecipeDao
import com.saffron.cook.core.database.entity.toCachedEntity
import com.saffron.cook.core.database.entity.toCategory
import com.saffron.cook.core.database.entity.toEntity
import com.saffron.cook.core.database.entity.toRecipe
import com.saffron.cook.core.domain.model.Category
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.repository.RecipeRepository

private const val MAX_CACHED_RECIPES = 200
private const val LIST_FALLBACK_LIMIT = 12
private const val SEARCH_FALLBACK_LIMIT = 25

/**
 * Network-first [RecipeRepository] with an opportunistic Room cache.
 * Full results (detail/search/featured) are upserted; partial list rows are
 * inserted only if absent so they never overwrite a full cached row.
 */
class OfflineFirstRecipeRepository(
    private val remote: RecipeRepository,
    private val cacheDao: CachedRecipeDao,
    private val savedDao: SavedRecipeDao,
    private val maxCachedRecipes: Int = MAX_CACHED_RECIPES,
) : RecipeRepository {

    override suspend fun getRecipes(): List<Recipe> {
        val recipes = runCatching { remote.getRecipes() }.getOrDefault(emptyList())
        if (recipes.isEmpty()) return cacheDao.getRecent(LIST_FALLBACK_LIMIT).map { it.toRecipe() }
        cachePartial(recipes)
        return recipes
    }

    override suspend fun getRecipesByCategory(categoryId: String): List<Recipe> {
        val recipes = runCatching { remote.getRecipesByCategory(categoryId) }.getOrDefault(emptyList())
        if (recipes.isEmpty()) return cacheDao.getByCategory(categoryId, LIST_FALLBACK_LIMIT).map { it.toRecipe() }
        cachePartial(recipes)
        return recipes
    }

    override suspend fun searchRecipes(query: String): List<Recipe> {
        val recipes = runCatching { remote.searchRecipes(query) }.getOrDefault(emptyList())
        if (recipes.isEmpty()) return cacheDao.searchByTitle(query, SEARCH_FALLBACK_LIMIT).map { it.toRecipe() }
        cacheFull(recipes)
        return recipes
    }

    override suspend fun getRecipeById(id: String): Recipe? {
        cacheDao.getById(id)?.takeIf { it.isFullDetail }?.let { return it.toRecipe() }
        savedDao.getById(id)?.toRecipe()?.takeIf { it.steps.isNotEmpty() }?.let { return it }
        // No full local copy — propagate remote failures so RecipeViewModel shows error/retry.
        val recipe = remote.getRecipeById(id) ?: return null
        cacheFull(listOf(recipe))
        return recipe
    }

    override suspend fun getFeaturedRecipe(): Recipe? {
        val recipe = runCatching { remote.getFeaturedRecipe() }.getOrNull()
        if (recipe == null) return cacheDao.getMostRecentFull()?.toRecipe()
        cacheFull(listOf(recipe))
        return recipe
    }

    override suspend fun getCategories(): List<Category> {
        val categories = runCatching { remote.getCategories() }.getOrDefault(emptyList())
        if (categories.isEmpty()) return cacheDao.getCategories().map { it.toCategory() }
        cacheDao.replaceCategories(categories.map { it.toEntity() })
        return categories
    }

    private suspend fun cachePartial(recipes: List<Recipe>) {
        val now = System.currentTimeMillis()
        recipes.forEach { cacheDao.insertPartialIfAbsent(it.toCachedEntity(cachedAt = now, isFullDetail = false)) }
        cacheDao.evictOldest(maxCachedRecipes)
    }

    private suspend fun cacheFull(recipes: List<Recipe>) {
        val now = System.currentTimeMillis()
        recipes.forEach { cacheDao.upsertFull(it.toCachedEntity(cachedAt = now, isFullDetail = true)) }
        cacheDao.evictOldest(maxCachedRecipes)
    }
}
