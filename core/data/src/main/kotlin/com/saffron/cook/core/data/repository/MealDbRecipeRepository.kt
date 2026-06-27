package com.saffron.cook.core.data.repository

import com.saffron.cook.core.data.network.TheMealDbService
import com.saffron.cook.core.data.network.toCategory
import com.saffron.cook.core.data.network.toPartialRecipe
import com.saffron.cook.core.data.network.toRecipe
import com.saffron.cook.core.domain.model.Category
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.repository.RecipeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class MealDbRecipeRepository(
    private val service: TheMealDbService,
) : RecipeRepository {
    private val preferredCategoryIds = listOf("chicken", "pasta", "seafood", "dessert")

    override suspend fun getRecipes(): List<Recipe> =
        coroutineScope {
            val liveCategories =
                runCatching {
                    service.getCategories().categories?.associate {
                        it.name.lowercase() to it.name
                    } ?: emptyMap()
                }.getOrDefault(emptyMap())

            preferredCategoryIds
                .map { id ->
                    async {
                        val apiName = liveCategories[id] ?: id.replaceFirstChar { it.uppercase() }
                        runCatching {
                            service
                                .filterByCategory(apiName)
                                .meals
                                ?.take(3)
                                ?.map { it.toPartialRecipe(id) }
                                ?: emptyList()
                        }.getOrDefault(emptyList())
                    }
                }.flatMap { it.await() }
        }

    override suspend fun getRecipeById(id: String): Recipe? =
        service
            .lookupMeal(id)
            .meals
            ?.firstOrNull()
            ?.toRecipe()

    override suspend fun getCategories(): List<Category> =
        runCatching {
            service.getCategories().categories?.map { it.toCategory() } ?: emptyList()
        }.getOrDefault(emptyList())

    override suspend fun getFeaturedRecipe(): Recipe? =
        runCatching {
            service
                .getRandomMeal()
                .meals
                ?.firstOrNull()
                ?.toRecipe(isFeatured = true)
        }.getOrNull()

    override suspend fun getRecipesByCategory(categoryId: String): List<Recipe> =
        runCatching {
            val apiCategory = categoryId.replaceFirstChar { it.uppercase() }
            service
                .filterByCategory(apiCategory)
                .meals
                ?.map { it.toPartialRecipe(categoryId) }
                ?: emptyList()
        }.getOrDefault(emptyList())

    override suspend fun searchRecipes(query: String): List<Recipe> =
        runCatching {
            service.searchMeals(query).meals?.map { it.toRecipe() } ?: emptyList()
        }.getOrDefault(emptyList())
}
