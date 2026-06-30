package com.saffron.cook.feature.note.detail

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.saffron.cook.core.testing.FakeRecipeNotesRepository
import com.saffron.cook.core.testing.Fixtures
import com.saffron.cook.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class NoteDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val notes = FakeRecipeNotesRepository()

    private fun viewModel(noteId: Long) =
        NoteDetailViewModel(SavedStateHandle(mapOf("noteId" to noteId)), notes)

    @Test
    fun `loads the note by id`() = runTest {
        val id = notes.upsert(Fixtures.note(title = "Tasty"))
        val vm = viewModel(id)
        advanceUntilIdle()
        assertThat(vm.uiState.value.note?.title).isEqualTo("Tasty")
        assertThat(vm.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `delete confirm toggles flag`() = runTest {
        val id = notes.upsert(Fixtures.note())
        val vm = viewModel(id)
        advanceUntilIdle()
        vm.onShowDeleteConfirm()
        assertThat(vm.uiState.value.showDeleteConfirm).isTrue()
        vm.onDismissDeleteConfirm()
        assertThat(vm.uiState.value.showDeleteConfirm).isFalse()
    }

    @Test
    fun `delete removes the note and invokes callback`() = runTest {
        val id = notes.upsert(Fixtures.note())
        val vm = viewModel(id)
        advanceUntilIdle()
        var deleted = false
        vm.onDelete { deleted = true }
        advanceUntilIdle()
        assertThat(deleted).isTrue()
        assertThat(notes.getNote(id)).isNull()
    }
}
