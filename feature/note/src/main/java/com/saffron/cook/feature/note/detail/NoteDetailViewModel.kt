package com.saffron.cook.feature.note.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saffron.cook.core.database.repository.RecipeNotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val notesRepository: RecipeNotesRepository,
) : ViewModel() {

    private val noteId: Long = checkNotNull(savedStateHandle["noteId"])

    private val _uiState = MutableStateFlow(NoteDetailUiState(isLoading = true))
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("d MMM", Locale.getDefault())

    init {
        viewModelScope.launch {
            notesRepository.observeNote(noteId).collect { entity ->
                if (entity != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            note = NoteDetailItem(
                                id = entity.id,
                                recipeId = entity.recipeId,
                                recipeName = entity.recipeName,
                                recipeImage = entity.recipeImage,
                                title = entity.title,
                                body = entity.body,
                                rating = entity.rating,
                                labels = RecipeNotesRepository.labelsFromString(entity.labels).toList(),
                                photos = RecipeNotesRepository.photosFromString(entity.photos),
                                dateLabel = dateFormatter.format(Date(entity.createdAt)),
                            ),
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, note = null) }
                }
            }
        }
    }

    fun onShowDeleteConfirm() = _uiState.update { it.copy(showDeleteConfirm = true) }
    fun onDismissDeleteConfirm() = _uiState.update { it.copy(showDeleteConfirm = false) }

    fun onDelete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            notesRepository.delete(noteId)
            onDeleted()
        }
    }
}
