package com.saffron.cook.feature.note.editor

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.saffron.cook.core.data.repository.fake.FakeRecipeRepository
import com.saffron.cook.core.testing.FakeRecipeNotesRepository
import com.saffron.cook.core.testing.Fixtures
import com.saffron.cook.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class NoteEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val notes = FakeRecipeNotesRepository()
    private val recipes = FakeRecipeRepository()

    private fun viewModel(recipeId: String = "1", noteId: Long = 0L) =
        NoteEditorViewModel(
            savedStateHandle = SavedStateHandle(mapOf("recipeId" to recipeId, "noteId" to noteId)),
            recipeRepository = recipes,
            notesRepository = notes,
        )

    @Test
    fun `create mode prefills recipe context`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        assertThat(vm.uiState.value.recipeName).isEqualTo("Cacio e Pepe")
        assertThat(vm.uiState.value.isEditMode).isFalse()
    }

    @Test
    fun `canSave is false when empty and true after typing`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        assertThat(vm.uiState.value.canSave).isFalse()
        vm.onBodyChange("Delicious")
        assertThat(vm.uiState.value.canSave).isTrue()
    }

    @Test
    fun `photos are capped at four`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onAddPhoto(listOf("a", "b", "c", "d", "e", "f"))
        assertThat(vm.uiState.value.photos).hasSize(4)
    }

    @Test
    fun `toggle label adds then removes`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onToggleLabel("spicy")
        assertThat(vm.uiState.value.labels).contains("spicy")
        vm.onToggleLabel("spicy")
        assertThat(vm.uiState.value.labels).doesNotContain("spicy")
    }

    @Test
    fun `save in create mode inserts a new note`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onBodyChange("Great")
        var done = false
        vm.onSave { done = true }
        advanceUntilIdle()
        assertThat(done).isTrue()
        assertThat(notes.allNotesFlow.value).hasSize(1)
    }

    @Test
    fun `edit mode loads existing note and preserves createdAt on save`() = runTest {
        val id = notes.upsert(Fixtures.note(id = 0L, title = "Old", createdAt = 555L))
        val vm = viewModel(noteId = id)
        advanceUntilIdle()
        assertThat(vm.uiState.value.isEditMode).isTrue()
        assertThat(vm.uiState.value.title).isEqualTo("Old")
        vm.onTitleChange("New")
        vm.onSave { }
        advanceUntilIdle()
        val saved = notes.getNote(id)
        assertThat(saved?.title).isEqualTo("New")
        assertThat(saved?.createdAt).isEqualTo(555L)
    }
}
