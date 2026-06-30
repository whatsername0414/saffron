package com.saffron.cook.feature.note.main

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

class NoteListViewModel(
    private val notesRepository: RecipeNotesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteListUiState(isLoading = true))
    val uiState: StateFlow<NoteListUiState> = _uiState.asStateFlow()

    private val dateFormatter = SimpleDateFormat("d MMM", Locale.getDefault())

    init {
        viewModelScope.launch {
            notesRepository.allNotesFlow.collect { entities ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notes = entities.map { e ->
                            NoteListItem(
                                id = e.id,
                                recipeName = e.recipeName,
                                recipeImage = e.recipeImage,
                                title = e.title,
                                body = e.body,
                                rating = e.rating,
                                labels = RecipeNotesRepository.labelsFromString(e.labels).toList(),
                                dateLabel = dateFormatter.format(Date(e.createdAt)),
                            )
                        },
                    )
                }
            }
        }
    }
}
