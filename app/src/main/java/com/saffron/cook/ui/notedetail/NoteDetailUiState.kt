package com.saffron.cook.ui.notedetail

data class NoteDetailItem(
    val id: Long,
    val recipeId: String,
    val recipeName: String,
    val recipeImage: String,
    val title: String,
    val body: String,
    val rating: Int,
    val labels: List<String>,
    val photos: List<String>,
    val dateLabel: String,
)

data class NoteDetailUiState(
    val note: NoteDetailItem? = null,
    val isLoading: Boolean = false,
    val showDeleteConfirm: Boolean = false,
)
