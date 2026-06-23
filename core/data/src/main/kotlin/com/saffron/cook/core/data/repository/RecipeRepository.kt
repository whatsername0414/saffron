package com.saffron.cook.core.data.repository

import com.saffron.cook.core.data.model.Category
import com.saffron.cook.core.data.model.Recipe

interface RecipeRepository {
    suspend fun getRecipes(): List<Recipe>

    suspend fun getRecipeById(id: String): Recipe?

    suspend fun getCategories(): List<Category>

    suspend fun getFeaturedRecipe(): Recipe?

    suspend fun getRecipesByCategory(categoryId: String): List<Recipe>

    suspend fun searchRecipes(query: String): List<Recipe>
}
