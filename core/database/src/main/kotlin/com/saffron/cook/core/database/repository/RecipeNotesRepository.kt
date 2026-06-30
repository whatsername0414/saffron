package com.saffron.cook.core.database.repository

import com.saffron.cook.core.database.dao.RecipeNoteDao
import com.saffron.cook.core.database.entity.RecipeNoteEntity
import kotlinx.coroutines.flow.Flow

class RecipeNotesRepository(private val dao: RecipeNoteDao) {

    val allNotesFlow: Flow<List<RecipeNoteEntity>> = dao.observeAll()
    val noteCountFlow: Flow<Int> = dao.observeCount()

    fun observeNote(id: Long): Flow<RecipeNoteEntity?> = dao.observeById(id)

    suspend fun upsert(entity: RecipeNoteEntity): Long =
        if (entity.id == 0L) dao.insert(entity) else { dao.update(entity); entity.id }

    suspend fun getNote(id: Long): RecipeNoteEntity? = dao.getById(id)

    suspend fun delete(id: Long) = dao.deleteById(id)

    companion object {
        fun labelsToString(labels: Set<String>): String = labels.joinToString(",")
        fun labelsFromString(s: String): Set<String> =
            if (s.isBlank()) emptySet() else s.split(",").toSet()

        fun photosToString(photos: List<String>): String = photos.joinToString(",")
        fun photosFromString(s: String): List<String> =
            if (s.isBlank()) emptyList() else s.split(",")
    }
}
