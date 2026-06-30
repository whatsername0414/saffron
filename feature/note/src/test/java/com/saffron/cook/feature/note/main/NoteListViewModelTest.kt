package com.saffron.cook.feature.note.main

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.saffron.cook.core.testing.FakeRecipeNotesRepository
import com.saffron.cook.core.testing.Fixtures
import com.saffron.cook.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `exposes all notes from the repository`() = runTest {
        val notes = FakeRecipeNotesRepository()
        notes.upsert(Fixtures.note(title = "A"))
        notes.upsert(Fixtures.note(title = "B"))
        val vm = NoteListViewModel(notes)
        advanceUntilIdle()
        assertThat(vm.uiState.value.notes).hasSize(2)
        assertThat(vm.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `starts with loading true and empty notes`() = runTest {
        val notes = FakeRecipeNotesRepository()
        val vm = NoteListViewModel(notes)
        advanceUntilIdle()
        assertThat(vm.uiState.value.isLoading).isFalse()
        assertThat(vm.uiState.value.notes).isEmpty()
    }

    @Test
    fun `note list items reflect repository data`() = runTest {
        val notes = FakeRecipeNotesRepository()
        notes.upsert(Fixtures.note(title = "Pasta night", rating = 4))
        val vm = NoteListViewModel(notes)
        advanceUntilIdle()
        val item = vm.uiState.value.notes.single()
        assertThat(item.title).isEqualTo("Pasta night")
        assertThat(item.rating).isEqualTo(4)
    }
}
