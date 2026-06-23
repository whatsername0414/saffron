package com.saffron.cook.core.data.model

data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val categoryId: String,
    val ingredients: List<Ingredient>,
    val steps: List<Step>,
    val isFeatured: Boolean = false,
    // TheMealDB doesn't provide these; null = not available
    val cookTimeMinutes: Int? = null,
    val servings: Int? = null,
    val difficulty: Difficulty? = null,
    val rating: Float? = null,
    val ratingCount: Int? = null,
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
