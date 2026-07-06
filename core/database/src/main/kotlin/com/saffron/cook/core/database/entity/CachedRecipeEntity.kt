package com.saffron.cook.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saffron.cook.core.domain.model.Difficulty
import com.saffron.cook.core.domain.model.Recipe

@Entity(tableName = "cached_recipes")
data class CachedRecipeEntity(
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
    val cachedAt: Long,
    val isFullDetail: Boolean,
)

fun Recipe.toCachedEntity(cachedAt: Long, isFullDetail: Boolean) = CachedRecipeEntity(
    id = id,
    title = title,
    imageUrl = imageUrl,
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    categoryId = categoryId,
    description = description,
    ingredients = RecipeColumnCodec.encodeIngredients(ingredients),
    steps = RecipeColumnCodec.encodeSteps(steps),
    isFeatured = isFeatured,
    difficulty = difficulty?.name,
    rating = rating,
    ratingCount = ratingCount,
    cachedAt = cachedAt,
    isFullDetail = isFullDetail,
)

fun CachedRecipeEntity.toRecipe() = Recipe(
    id = id,
    title = title,
    imageUrl = imageUrl,
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    categoryId = categoryId,
    description = description,
    ingredients = RecipeColumnCodec.decodeIngredients(ingredients),
    steps = RecipeColumnCodec.decodeSteps(steps),
    isFeatured = isFeatured,
    difficulty = difficulty?.let { runCatching { Difficulty.valueOf(it) }.getOrNull() },
    rating = rating,
    ratingCount = ratingCount,
)
