package com.saffron.cook.ui.notes

data class NoteEditorUiState(
    val isLoading: Boolean = false,
    val recipeName: String = "",
    val recipeImage: String = "",
    val title: String = "",
    val body: String = "",
    val rating: Int = 0,
    val labels: Set<String> = emptySet(),
    val photos: List<String> = emptyList(),
    val isSaving: Boolean = false,
) {
    val canSave: Boolean
        get() = title.isNotBlank() || body.isNotBlank() || rating > 0 || labels.isNotEmpty() || photos.isNotEmpty()
}
