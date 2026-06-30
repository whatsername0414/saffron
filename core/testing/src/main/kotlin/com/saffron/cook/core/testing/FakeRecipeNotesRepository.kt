package com.saffron.cook.core.testing

import com.saffron.cook.core.database.entity.RecipeNoteEntity
import com.saffron.cook.core.database.repository.RecipeNotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeRecipeNotesRepository : RecipeNotesRepository {
    private val notes = MutableStateFlow<List<RecipeNoteEntity>>(emptyList())
    private var nextId = 1L

    override val allNotesFlow: Flow<List<RecipeNoteEntity>> = notes.asStateFlow()
    override val noteCountFlow: Flow<Int> = notes.map { it.size }

    override fun observeNote(id: Long): Flow<RecipeNoteEntity?> =
        notes.map { list -> list.firstOrNull { it.id == id } }

    override suspend fun upsert(entity: RecipeNoteEntity): Long {
        return if (entity.id == 0L) {
            val withId = entity.copy(id = nextId++)
            notes.update { it + withId }
            withId.id
        } else {
            notes.update { list -> list.map { if (it.id == entity.id) entity else it } }
            entity.id
        }
    }

    override suspend fun getNote(id: Long): RecipeNoteEntity? =
        notes.value.firstOrNull { it.id == id }

    override suspend fun delete(id: Long) {
        notes.update { list -> list.filterNot { it.id == id } }
    }
}
