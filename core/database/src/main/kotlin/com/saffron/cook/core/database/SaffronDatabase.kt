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
    version = 5,
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

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE saved_recipes ADD COLUMN description TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE saved_recipes ADD COLUMN ingredients TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE saved_recipes ADD COLUMN steps TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE saved_recipes ADD COLUMN isFeatured INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE saved_recipes ADD COLUMN difficulty TEXT")
                db.execSQL("ALTER TABLE saved_recipes ADD COLUMN rating REAL")
                db.execSQL("ALTER TABLE saved_recipes ADD COLUMN ratingCount INTEGER")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cooked_recipes ADD COLUMN description TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE cooked_recipes ADD COLUMN ingredients TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE cooked_recipes ADD COLUMN steps TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE cooked_recipes ADD COLUMN cookTimeMinutes INTEGER")
                db.execSQL("ALTER TABLE cooked_recipes ADD COLUMN servings INTEGER")
                db.execSQL("ALTER TABLE cooked_recipes ADD COLUMN isFeatured INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE cooked_recipes ADD COLUMN difficulty TEXT")
                db.execSQL("ALTER TABLE cooked_recipes ADD COLUMN rating REAL")
                db.execSQL("ALTER TABLE cooked_recipes ADD COLUMN ratingCount INTEGER")
            }
        }
    }
}
