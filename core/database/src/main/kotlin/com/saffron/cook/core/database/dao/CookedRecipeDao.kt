package com.saffron.cook.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saffron.cook.core.database.entity.CookedRecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CookedRecipeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: CookedRecipeEntity): Long

    @Query("UPDATE cooked_recipes SET times = times + 1, lastCookedAt = :timestamp WHERE recipeId = :id")
    suspend fun incrementAndTouch(id: String, timestamp: Long)

    @Query("SELECT * FROM cooked_recipes ORDER BY lastCookedAt DESC")
    fun observeAll(): Flow<List<CookedRecipeEntity>>

    @Query("SELECT COALESCE(SUM(times), 0) FROM cooked_recipes")
    fun observeTotalCount(): Flow<Int>
}
