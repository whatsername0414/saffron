package com.saffron.cook.core.testing

import com.saffron.cook.core.database.entity.CookedRecipeEntity
import com.saffron.cook.core.database.entity.RecipeNoteEntity
import com.saffron.cook.core.domain.model.Ingredient
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.model.Step

object Fixtures {
    fun recipe(
        id: String = "1",
        title: String = "Test Recipe",
        categoryId: String = "italian",
        ingredients: List<Ingredient> = listOf(Ingredient("1 cup", "flour")),
        steps: List<Step> = listOf(Step("Mix", "Stir for 2 minutes")),
        isFeatured: Boolean = false,
    ): Recipe = Recipe(
        id = id,
        title = title,
        description = "A test recipe",
        imageUrl = "https://example.com/$id.jpg",
        categoryId = categoryId,
        ingredients = ingredients,
        steps = steps,
        isFeatured = isFeatured,
        cookTimeMinutes = 20,
        servings = 2,
    )

    fun note(
        id: Long = 0L,
        recipeId: String = "1",
        title: String = "Note title",
        body: String = "Note body",
        rating: Int = 0,
        labels: String = "",
        photos: String = "",
        createdAt: Long = 1_000L,
    ): RecipeNoteEntity = RecipeNoteEntity(
        id = id,
        recipeId = recipeId,
        recipeName = "Test Recipe",
        recipeImage = "https://example.com/$recipeId.jpg",
        title = title,
        body = body,
        rating = rating,
        labels = labels,
        photos = photos,
        createdAt = createdAt,
    )

    fun cooked(
        recipeId: String = "1",
        times: Int = 1,
        lastCookedAt: Long = 1_000L,
    ): CookedRecipeEntity = CookedRecipeEntity(
        recipeId = recipeId,
        recipeName = "Test Recipe",
        recipeImage = "https://example.com/$recipeId.jpg",
        recipeCategory = "italian",
        times = times,
        lastCookedAt = lastCookedAt,
    )
}
