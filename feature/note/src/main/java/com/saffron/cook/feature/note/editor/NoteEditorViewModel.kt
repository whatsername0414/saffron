package com.saffron.cook.feature.note.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saffron.cook.core.database.repository.RecipeNotesRepository
import com.saffron.cook.core.database.entity.RecipeNoteEntity
import com.saffron.cook.core.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val recipeRepository: RecipeRepository,
    private val notesRepository: RecipeNotesRepository,
) : ViewModel() {

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"])
    private val editNoteId: Long = savedStateHandle["noteId"] ?: 0L

    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            if (editNoteId != 0L) {
                val note = notesRepository.getNote(editNoteId)
                if (note != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            existingNoteId = note.id,
                            existingCreatedAt = note.createdAt,
                            recipeName = note.recipeName,
                            recipeImage = note.recipeImage,
                            title = note.title,
                            body = note.body,
                            rating = note.rating,
                            labels = RecipeNotesRepository.labelsFromString(note.labels),
                            photos = RecipeNotesRepository.photosFromString(note.photos),
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } else {
                try {
                    val recipe = recipeRepository.getRecipeById(recipeId)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recipeName = recipe?.title.orEmpty(),
                            recipeImage = recipe?.imageUrl.orEmpty(),
                        )
                    }
                } catch (_: Exception) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onBodyChange(value: String) = _uiState.update { it.copy(body = value) }
    fun onRatingChange(value: Int) = _uiState.update { it.copy(rating = value) }

    fun onToggleLabel(label: String) = _uiState.update { s ->
        val updated = if (label in s.labels) s.labels - label else s.labels + label
        s.copy(labels = updated)
    }

    fun onAddPhoto(uris: List<String>) = _uiState.update { s ->
        s.copy(photos = (s.photos + uris).take(4))
    }

    fun onRemovePhoto(index: Int) = _uiState.update { s ->
        s.copy(photos = s.photos.filterIndexed { i, _ -> i != index })
    }

    fun onSave(onDone: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            notesRepository.upsert(
                RecipeNoteEntity(
                    id = state.existingNoteId,
                    createdAt = if (state.isEditMode) state.existingCreatedAt else System.currentTimeMillis(),
                    recipeId = recipeId,
                    recipeName = state.recipeName,
                    recipeImage = state.recipeImage,
                    title = state.title,
                    body = state.body,
                    rating = state.rating,
                    labels = RecipeNotesRepository.labelsToString(state.labels),
                    photos = RecipeNotesRepository.photosToString(state.photos),
                ),
            )
            onDone()
        }
    }
}
