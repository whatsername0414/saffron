package com.saffron.cook.core.data.model

data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val categoryId: String,
    val cookTimeMinutes: Int,
    val servings: Int,
    val difficulty: Difficulty,
    val rating: Float,
    val ratingCount: Int,
    val ingredients: List<Ingredient>,
    val steps: List<Step>,
    val isFeatured: Boolean = false,
)

data class Ingredient(
    val amount: String,
    val name: String,
)

data class Step(
    val title: String,
    val instruction: String,
)

enum class Difficulty { Easy, Medium, Hard }
