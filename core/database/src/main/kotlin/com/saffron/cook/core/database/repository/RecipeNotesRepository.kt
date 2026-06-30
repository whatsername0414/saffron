package com.saffron.cook.core.database.repository

import com.saffron.cook.core.database.entity.RecipeNoteEntity
import kotlinx.coroutines.flow.Flow

interface RecipeNotesRepository {
    val allNotesFlow: Flow<List<RecipeNoteEntity>>
    val noteCountFlow: Flow<Int>

    fun observeNote(id: Long): Flow<RecipeNoteEntity?>
    suspend fun upsert(entity: RecipeNoteEntity): Long
    suspend fun getNote(id: Long): RecipeNoteEntity?
    suspend fun delete(id: Long)

    companion object {
        fun labelsToString(labels: Set<String>): String = labels.joinToString(",")
        fun labelsFromString(s: String): Set<String> =
            if (s.isBlank()) emptySet() else s.split(",").toSet()

        fun photosToString(photos: List<String>): String = photos.joinToString(",")
        fun photosFromString(s: String): List<String> =
            if (s.isBlank()) emptyList() else s.split(",")
    }
}
