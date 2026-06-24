package com.saffron.cook.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [SavedRecipeEntity::class, RecipeNoteEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class SaffronDatabase : RoomDatabase() {
    abstract fun savedRecipeDao(): SavedRecipeDao
    abstract fun recipeNoteDao(): RecipeNoteDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `recipe_notes` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `recipeId` TEXT NOT NULL,
                        `recipeName` TEXT NOT NULL,
                        `recipeImage` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `body` TEXT NOT NULL,
                        `rating` INTEGER NOT NULL,
                        `labels` TEXT NOT NULL,
                        `photos` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )""",
                )
            }
        }
    }
}
