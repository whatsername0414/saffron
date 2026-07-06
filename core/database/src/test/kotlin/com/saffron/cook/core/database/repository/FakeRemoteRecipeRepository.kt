package com.saffron.cook.core.database.repository

import com.saffron.cook.core.domain.model.Category
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.repository.RecipeRepository
import java.io.IOException

/** Minimal in-memory remote for offline-cache tests; flip [shouldThrow] to simulate being offline. */
internal class FakeRemoteRecipeRepository : RecipeRepository {
    var shouldThrow = false
    var recipes: List<Recipe> = emptyList()
    var recipeById: Recipe? = null
    var categories: List<Category> = emptyList()
    var featured: Recipe? = null
    var recipeByIdCalls = 0

    private fun failIfOffline() {
        if (shouldThrow) throw IOException("offline")
    }

    override suspend fun getRecipes(): List<Recipe> {
        failIfOffline()
        return recipes
    }

    override suspend fun getRecipeById(id: String): Recipe? {
        recipeByIdCalls++
        failIfOffline()
        return recipeById
    }

    override suspend fun getCategories(): List<Category> {
        failIfOffline()
        return categories
    }

    override suspend fun getFeaturedRecipe(): Recipe? {
        failIfOffline()
        return featured
    }

    override suspend fun getRecipesByCategory(categoryId: String): List<Recipe> {
        failIfOffline()
        return recipes.filter { it.categoryId == categoryId }
    }

    override suspend fun searchRecipes(query: String): List<Recipe> {
        failIfOffline()
        return recipes.filter { it.title.contains(query, ignoreCase = true) }
    }
}
