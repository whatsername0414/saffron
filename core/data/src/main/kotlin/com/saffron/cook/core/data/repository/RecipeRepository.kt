package com.saffron.cook.core.data.repository

import com.saffron.cook.core.data.model.Category
import com.saffron.cook.core.data.model.Recipe

interface RecipeRepository {
    fun getRecipes(): List<Recipe>
    fun getRecipeById(id: String): Recipe?
    fun getCategories(): List<Category>
    fun getFeaturedRecipe(): Recipe?
    fun getRecipesByCategory(categoryId: String): List<Recipe>
    fun searchRecipes(query: String): List<Recipe>
}
