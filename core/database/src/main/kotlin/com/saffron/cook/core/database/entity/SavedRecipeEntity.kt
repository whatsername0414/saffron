package com.saffron.cook.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saffron.cook.core.domain.model.Difficulty
import com.saffron.cook.core.domain.model.Ingredient
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.model.Step

// U+001E (record separator) between items, U+001F (unit separator) between fields within an item.
// These characters never appear in recipe text from TheMealDB.
private const val ITEM_SEP = ""
private const val FIELD_SEP = ""

@Entity(tableName = "saved_recipes")
data class SavedRecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String,
    val cookTimeMinutes: Int?,
    val servings: Int?,
    val categoryId: String,
    val description: String,
    val ingredients: String,
    val steps: String,
    val isFeatured: Boolean,
    val difficulty: String?,
    val rating: Float?,
    val ratingCount: Int?,
)

fun Recipe.toEntity() = SavedRecipeEntity(
    id = id,
    title = title,
    imageUrl = imageUrl,
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    categoryId = categoryId,
    description = description,
    ingredients = ingredients.joinToString(ITEM_SEP) { "${it.amount}$FIELD_SEP${it.name}" },
    steps = steps.joinToString(ITEM_SEP) { "${it.title}$FIELD_SEP${it.instruction}" },
    isFeatured = isFeatured,
    difficulty = difficulty?.name,
    rating = rating,
    ratingCount = ratingCount,
)

fun SavedRecipeEntity.toRecipe() = Recipe(
    id = id,
    title = title,
    imageUrl = imageUrl,
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    categoryId = categoryId,
    description = description,
    ingredients = ingredients.toIngredients(),
    steps = steps.toSteps(),
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
