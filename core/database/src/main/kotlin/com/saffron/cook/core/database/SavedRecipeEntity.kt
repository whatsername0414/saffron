package com.saffron.cook.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saffron.cook.core.domain.model.Recipe

@Entity(tableName = "saved_recipes")
data class SavedRecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String,
    val cookTimeMinutes: Int?,
    val servings: Int?,
    val categoryId: String,
)

fun Recipe.toEntity() = SavedRecipeEntity(
    id = id,
    title = title,
    imageUrl = imageUrl,
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    categoryId = categoryId,
)

fun SavedRecipeEntity.toRecipe() = Recipe(
    id = id,
    title = title,
    imageUrl = imageUrl,
    cookTimeMinutes = cookTimeMinutes,
    servings = servings,
    categoryId = categoryId,
    description = "",
    ingredients = emptyList(),
    steps = emptyList(),
)
