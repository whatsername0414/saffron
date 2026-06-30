package com.saffron.cook.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_notes")
data class RecipeNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: String,
    val recipeName: String,
    val recipeImage: String,
    val title: String,
    val body: String,
    val rating: Int,
    val labels: String,
    val photos: String,
    val createdAt: Long = System.currentTimeMillis(),
)
