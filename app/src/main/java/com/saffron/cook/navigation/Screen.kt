package com.saffron.cook.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object Profile : Screen("profile")
    data object RecipeDetail : Screen("recipe/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe/$recipeId"
    }
    data object CookingMode : Screen("cooking/{recipeId}") {
        fun createRoute(recipeId: String) = "cooking/$recipeId"
    }
    data object CookedList : Screen("cooked_list")
    data object NotesList : Screen("notes_list")
    data object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Long) = "note_detail/$noteId"
    }
    data object NoteEditor : Screen("note_editor/{recipeId}?noteId={noteId}") {
        fun createRoute(recipeId: String) = "note_editor/$recipeId"
        fun createEditRoute(recipeId: String, noteId: Long) = "note_editor/$recipeId?noteId=$noteId"
    }
}
