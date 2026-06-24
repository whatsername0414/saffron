package com.saffron.cook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SavedRecipeEntity::class], version = 1, exportSchema = false)
abstract class SaffronDatabase : RoomDatabase() {
    abstract fun savedRecipeDao(): SavedRecipeDao
}
