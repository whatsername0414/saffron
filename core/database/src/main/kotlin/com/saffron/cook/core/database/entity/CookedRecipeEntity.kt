package com.saffron.cook.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saffron.cook.core.domain.model.Difficulty
import com.saffron.cook.core.domain.model.Ingredient
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.model.Step

// Reuse the same delimiters as SavedRecipeEntity.
private const val ITEM_SEP = ""
private const val FIELD_SEP = ""

@Entity(tableName = "cooked_recipes")
data class CookedRecipeEntity(
    @PrimaryKey val recipeId: String,
    val recipeName: String,
    val recipeImage: String,
    val recipeCategory: String,
    val times: Int = 1,
    val lastCookedAt: Long = System.currentTimeMillis(),
    val description: String = "",
    val ingredients: String = "",
    val steps: String = "",
    val cookTimeMinutes: Int? = null,
    val servings: Int? = null,
    val isFeatured: Boolean = false,
    val difficulty: String? = null,
    val rating: Float? = null,
    val ratingCount: Int? = null,
)

fun Recipe.toCookedEntity() = CookedRecipeEntity(
    recipeId = id,
    recipeName = title,
    recipeImage = imageUrl,
    recipeCategory = categoryId,
    description = description,
    ingredients = ingredients.joinToString(ITEM_SEP) { "${it.amount}$FIELD_SEP${it.name}" },
    steps = steps.joinToString(ITEM_SEP) { "${it.title}$FIELD_SEP${it.instruction}" },
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    isFeatured = isFeatured,
    difficulty = difficulty?.name,
    rating = rating,
    ratingCount = ratingCount,
)

fun CookedRecipeEntity.toRecipe() = Recipe(
    id = recipeId,
    title = recipeName,
    imageUrl = recipeImage,
    categoryId = recipeCategory,
    description = description,
    ingredients = ingredients.toIngredients(),
    steps = steps.toSteps(),
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    isFeatured = isFeatured,
    difficulty = difficulty?.let { runCatching { Difficulty.valueOf(it) }.getOrNull() },
    rating = rating,
    ratingCount = ratingCount,
)

private fun String.toIngredients(): List<Ingredient> =
    if (isEmpty()) emptyList()
    else split(ITEM_SEP).map {
        val parts = it.split(FIELD_SEP, limit = 2)
        Ingredient(parts.getOrElse(0) { "" }, parts.getOrElse(1) { "" })
    }

private fun String.toSteps(): List<Step> =
    if (isEmpty()) emptyList()
    else split(ITEM_SEP).map {
        val parts = it.split(FIELD_SEP, limit = 2)
        Step(parts.getOrElse(0) { "" }, parts.getOrElse(1) { "" })
    }
