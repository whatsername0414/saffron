package com.saffron.cook.ui.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saffron.cook.data.RecipeNotesRepository
import com.saffron.cook.data.local.RecipeNoteEntity
import com.saffron.cook.core.data.repository.RecipeRepository
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

    private val _uiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val recipe = recipeRepository.getRecipeById(recipeId)
                _uiState.update { it.copy(isLoading = false, recipeName = recipe?.title.orEmpty(), recipeImage = recipe?.imageUrl.orEmpty()) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false) }
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
