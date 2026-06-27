package com.saffron.cook.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedRecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SavedRecipeEntity)

    @Query("DELETE FROM saved_recipes WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM saved_recipes WHERE id = :id")
    suspend fun count(id: String): Int

    @Query("SELECT * FROM saved_recipes")
    fun observeAll(): Flow<List<SavedRecipeEntity>>

    @Query("SELECT id FROM saved_recipes")
    fun observeIds(): Flow<List<String>>
}
