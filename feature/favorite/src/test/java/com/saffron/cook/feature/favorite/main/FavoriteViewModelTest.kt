package com.saffron.cook.feature.favorite.main

import com.google.common.truth.Truth.assertThat
import com.saffron.cook.core.testing.FakeSavedRecipesRepository
import com.saffron.cook.core.testing.Fixtures
import com.saffron.cook.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class FavoriteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `exposes saved recipes from the repository`() = runTest {
        val saved = FakeSavedRecipesRepository()
        saved.seed(Fixtures.recipe(id = "1"), Fixtures.recipe(id = "2"))
        val vm = FavoriteViewModel(saved)
        advanceUntilIdle()
        assertThat(vm.uiState.value.recipes.map { it.id }).containsExactly("1", "2")
    }

    @Test
    fun `unsaving removes the recipe from state`() = runTest {
        val saved = FakeSavedRecipesRepository()
        saved.seed(Fixtures.recipe(id = "1"))
        val vm = FavoriteViewModel(saved)
        advanceUntilIdle()
        vm.onToggleSave("1")
        advanceUntilIdle()
        assertThat(vm.uiState.value.recipes).isEmpty()
    }
}
