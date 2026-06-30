package com.saffron.cook.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.saffron.cook.core.database.dao.CookedRecipeDao
import com.saffron.cook.core.database.dao.RecipeNoteDao
import com.saffron.cook.core.database.dao.SavedRecipeDao
import com.saffron.cook.core.database.entity.CookedRecipeEntity
import com.saffron.cook.core.database.entity.RecipeNoteEntity
import com.saffron.cook.core.database.entity.SavedRecipeEntity

@Database(
    entities = [SavedRecipeEntity::class, RecipeNoteEntity::class, CookedRecipeEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class SaffronDatabase : RoomDatabase() {
    abstract fun savedRecipeDao(): SavedRecipeDao
    abstract fun recipeNoteDao(): RecipeNoteDao
    abstract fun cookedRecipeDao(): CookedRecipeDao

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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `cooked_recipes` (
                        `recipeId` TEXT PRIMARY KEY NOT NULL,
                        `recipeName` TEXT NOT NULL,
                        `recipeImage` TEXT NOT NULL,
                        `recipeCategory` TEXT NOT NULL,
                        `times` INTEGER NOT NULL DEFAULT 1,
                        `lastCookedAt` INTEGER NOT NULL
                    )""",
                )
            }
        }
    }
}
