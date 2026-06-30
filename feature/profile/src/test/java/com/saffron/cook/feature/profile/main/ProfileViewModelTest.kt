package com.saffron.cook.feature.profile.main

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.saffron.cook.core.testing.FakeAuthRepository
import com.saffron.cook.core.testing.FakeCookedRecipesRepository
import com.saffron.cook.core.testing.FakeRecipeNotesRepository
import com.saffron.cook.core.testing.FakeSavedRecipesRepository
import com.saffron.cook.core.testing.Fixtures
import com.saffron.cook.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun buildViewModel(
        saved: FakeSavedRecipesRepository = FakeSavedRecipesRepository(),
        notes: FakeRecipeNotesRepository = FakeRecipeNotesRepository(),
        cooked: FakeCookedRecipesRepository = FakeCookedRecipesRepository(),
    ) = ProfileViewModel(FakeAuthRepository(), saved, notes, cooked)

    @Test
    fun `savedCount reflects seeded saved recipes`() = runTest {
        val saved = FakeSavedRecipesRepository().also {
            it.seed(Fixtures.recipe(id = "1"), Fixtures.recipe(id = "2"))
        }
        val vm = buildViewModel(saved = saved)
        advanceUntilIdle()
        assertThat(vm.uiState.value.savedCount).isEqualTo(2)
    }

    @Test
    fun `cookedCount reflects recorded cooked recipes`() = runTest {
        val cooked = FakeCookedRecipesRepository()
        val vm = buildViewModel(cooked = cooked)
        cooked.recordCooked(Fixtures.recipe(id = "1"))
        advanceUntilIdle()
        assertThat(vm.uiState.value.cookedCount).isEqualTo(1)
    }

    @Test
    fun `notesCount reflects upserted notes`() = runTest {
        val notes = FakeRecipeNotesRepository().also {
            it.upsert(Fixtures.note(title = "First"))
        }
        val vm = buildViewModel(notes = notes)
        advanceUntilIdle()
        assertThat(vm.uiState.value.notesCount).isEqualTo(1)
    }

    @Test
    fun `all counts update together`() = runTest {
        val saved = FakeSavedRecipesRepository().also {
            it.seed(Fixtures.recipe(id = "1"), Fixtures.recipe(id = "2"))
        }
        val cooked = FakeCookedRecipesRepository()
        val notes = FakeRecipeNotesRepository().also {
            it.upsert(Fixtures.note())
        }
        val vm = buildViewModel(saved = saved, notes = notes, cooked = cooked)
        cooked.recordCooked(Fixtures.recipe(id = "1"))
        advanceUntilIdle()
        assertThat(vm.uiState.value.savedCount).isEqualTo(2)
        assertThat(vm.uiState.value.cookedCount).isEqualTo(1)
        assertThat(vm.uiState.value.notesCount).isEqualTo(1)
    }

    @Test
    fun `user is null when not signed in`() = runTest {
        val vm = buildViewModel()
        advanceUntilIdle()
        assertThat(vm.uiState.value.user).isNull()
    }
}
