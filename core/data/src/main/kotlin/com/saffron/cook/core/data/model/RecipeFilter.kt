package com.saffron.cook.core.data.model

enum class RecipeFilter(val categoryId: String?) {
    All(null),
    Breakfast("breakfast"),
    Lunch("lunch"),
    Dinner("dinner"),
    Baking("baking"),
}
