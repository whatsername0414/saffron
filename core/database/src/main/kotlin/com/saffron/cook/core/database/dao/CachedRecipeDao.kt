package com.saffron.cook.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.saffron.cook.core.database.entity.CachedCategoryEntity
import com.saffron.cook.core.database.entity.CachedRecipeEntity

@Dao
interface CachedRecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFull(entity: CachedRecipeEntity)

    // Partial list rows must never clobber a full cached row (REPLACE would wipe steps).
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPartialIfAbsent(entity: CachedRecipeEntity)

    @Query("SELECT * FROM cached_recipes WHERE id = :id")
    suspend fun getById(id: String): CachedRecipeEntity?

    @Query("SELECT * FROM cached_recipes WHERE categoryId = :categoryId ORDER BY cachedAt DESC LIMIT :limit")
    suspend fun getByCategory(categoryId: String, limit: Int): List<CachedRecipeEntity>

    @Query("SELECT * FROM cached_recipes WHERE title LIKE '%' || :query || '%' ORDER BY cachedAt DESC LIMIT :limit")
    suspend fun searchByTitle(query: String, limit: Int): List<CachedRecipeEntity>

    @Query("SELECT * FROM cached_recipes ORDER BY cachedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<CachedRecipeEntity>

    @Query("SELECT * FROM cached_recipes WHERE isFullDetail = 1 ORDER BY cachedAt DESC LIMIT 1")
    suspend fun getMostRecentFull(): CachedRecipeEntity?

    @Query("DELETE FROM cached_recipes WHERE id NOT IN (SELECT id FROM cached_recipes ORDER BY cachedAt DESC LIMIT :keep)")
    suspend fun evictOldest(keep: Int)

    @Query("SELECT * FROM cached_categories")
    suspend fun getCategories(): List<CachedCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(entities: List<CachedCategoryEntity>)

    @Query("DELETE FROM cached_categories")
    suspend fun clearCategories()

    @Transaction
    suspend fun replaceCategories(entities: List<CachedCategoryEntity>) {
        clearCategories()
        insertCategories(entities)
    }
}
