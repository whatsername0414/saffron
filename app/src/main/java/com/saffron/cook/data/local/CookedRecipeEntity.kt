package com.saffron.cook.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cooked_recipes")
data class CookedRecipeEntity(
    @PrimaryKey val recipeId: String,
    val recipeName: String,
    val recipeImage: String,
    val recipeCategory: String,
    val times: Int = 1,
    val lastCookedAt: Long = System.currentTimeMillis(),
)
