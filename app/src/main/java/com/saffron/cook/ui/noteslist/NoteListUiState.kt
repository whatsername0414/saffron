package com.saffron.cook.ui.noteslist

import androidx.compose.runtime.Stable

data class NoteListItem(
    val id: Long,
    val recipeName: String,
    val recipeImage: String,
    val title: String,
    val body: String,
    val rating: Int,
    val labels: List<String>,
    val dateLabel: String,
)

@Stable
data class NoteListUiState(
    val notes: List<NoteListItem> = emptyList(),
    val isLoading: Boolean = false,
)
