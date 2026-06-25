package com.saffron.cook.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecipeNoteEntity): Long

    @Update
    suspend fun update(entity: RecipeNoteEntity)

    @Query("DELETE FROM recipe_notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM recipe_notes ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<RecipeNoteEntity>>

    @Query("SELECT * FROM recipe_notes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): RecipeNoteEntity?

    @Query("SELECT * FROM recipe_notes WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<RecipeNoteEntity?>

    @Query("SELECT COUNT(*) FROM recipe_notes")
    fun observeCount(): Flow<Int>
}
