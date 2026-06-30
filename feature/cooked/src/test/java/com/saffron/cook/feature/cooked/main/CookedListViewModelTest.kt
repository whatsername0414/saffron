package com.saffron.cook.feature.cooked.main

import com.google.common.truth.Truth.assertThat
import com.saffron.cook.core.testing.FakeCookedRecipesRepository
import com.saffron.cook.core.testing.FakeSavedRecipesRepository
import com.saffron.cook.core.testing.Fixtures
import com.saffron.cook.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class CookedListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `maps cooked entities to items with labels and total`() = runTest {
        val cooked = FakeCookedRecipesRepository()
        cooked.recordCooked(Fixtures.recipe(id = "1"))
        cooked.recordCooked(Fixtures.recipe(id = "1")) // times = 2
        cooked.recordCooked(Fixtures.recipe(id = "2")) // times = 1
        val vm = CookedListViewModel(cooked, FakeSavedRecipesRepository())
        advanceUntilIdle()
        val state = vm.uiState.value
        assertThat(state.totalCooked).isEqualTo(3)
        val first = state.items.first { it.recipeId == "1" }
        assertThat(first.timesLabel).isEqualTo("2 times")
        val second = state.items.first { it.recipeId == "2" }
        assertThat(second.timesLabel).isEqualTo("Once")
    }

    @Test
    fun `isSaved reflects the saved repository`() = runTest {
        val cooked = FakeCookedRecipesRepository()
        cooked.recordCooked(Fixtures.recipe(id = "1"))
        val saved = FakeSavedRecipesRepository()
        saved.seed(Fixtures.recipe(id = "1"))
        val vm = CookedListViewModel(cooked, saved)
        advanceUntilIdle()
        assertThat(vm.uiState.value.items.first().isSaved).isTrue()
    }
}
