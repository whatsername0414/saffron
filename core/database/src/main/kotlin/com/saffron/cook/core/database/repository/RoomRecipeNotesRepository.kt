package com.saffron.cook.core.database.repository

import com.saffron.cook.core.database.dao.RecipeNoteDao
import com.saffron.cook.core.database.entity.RecipeNoteEntity
import kotlinx.coroutines.flow.Flow

class RoomRecipeNotesRepository(private val dao: RecipeNoteDao) : RecipeNotesRepository {

    override val allNotesFlow: Flow<List<RecipeNoteEntity>> = dao.observeAll()
    override val noteCountFlow: Flow<Int> = dao.observeCount()

    override fun observeNote(id: Long): Flow<RecipeNoteEntity?> = dao.observeById(id)

    override suspend fun upsert(entity: RecipeNoteEntity): Long =
        if (entity.id == 0L) dao.insert(entity) else { dao.update(entity); entity.id }

    override suspend fun getNote(id: Long): RecipeNoteEntity? = dao.getById(id)

    override suspend fun delete(id: Long) = dao.deleteById(id)
}
