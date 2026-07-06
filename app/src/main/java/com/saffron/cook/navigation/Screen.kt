package com.saffron.cook.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Welcome : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data object Favorites : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object CookedList : Screen

    @Serializable
    data object NotesList : Screen

    @Serializable
    data class RecipeDetail(val recipeId: String) : Screen

    @Serializable
    data class CookingMode(val recipeId: String) : Screen

    @Serializable
    data class NoteDetail(val noteId: Long) : Screen

    @Serializable
    data class NoteEditor(val recipeId: String, val noteId: Long = 0L) : Screen
}
