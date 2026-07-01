package com.saffron.cook.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.saffron.cook.core.database.dao.CookedRecipeDao
import com.saffron.cook.core.database.dao.RecipeNoteDao
import com.saffron.cook.core.database.dao.SavedRecipeDao
import com.saffron.cook.core.database.entity.CookedRecipeEntity
import com.saffron.cook.core.database.entity.RecipeNoteEntity
import com.saffron.cook.core.database.entity.SavedRecipeEntity

@Database(
    entities = [SavedRecipeEntity::class, RecipeNoteEntity::class, CookedRecipeEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class SaffronDatabase : RoomDatabase() {
    abstract fun savedRecipeDao(): SavedRecipeDao
    abstract fun recipeNoteDao(): RecipeNoteDao
    abstract fun cookedRecipeDao(): CookedRecipeDao
}
