package com.saffron.cook.feature.home.main

import com.google.common.truth.Truth.assertThat
import com.saffron.cook.core.data.repository.fake.FakeRecipeRepository
import com.saffron.cook.core.testing.FakeSavedRecipesRepository
import com.saffron.cook.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val saved = FakeSavedRecipesRepository()

    private fun viewModel(repo: FakeRecipeRepository = FakeRecipeRepository()) =
        HomeViewModel(repository = repo, savedRecipesRepository = saved)

    @Test
    fun `loads featured categories and grid excluding featured`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        val state = vm.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.featuredRecipe).isNotNull()
        assertThat(state.categories).isNotEmpty()
        assertThat(state.recipes.none { it.id == state.featuredRecipe?.id }).isTrue()
    }

    @Test
    fun `selecting a category filters the grid`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onSelectCategory("italian")
        advanceUntilIdle()
        assertThat(vm.uiState.value.recipes.all { it.categoryId == "italian" }).isTrue()
        assertThat(vm.uiState.value.selectedCategoryId).isEqualTo("italian")
    }

    @Test
    fun `re-selecting the same category clears the filter`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onSelectCategory("italian")
        advanceUntilIdle()
        vm.onSelectCategory("italian")
        advanceUntilIdle()
        assertThat(vm.uiState.value.selectedCategoryId).isNull()
    }

    @Test
    fun `toggle save reflects in savedIds`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        val target = vm.uiState.value.recipes.first()
        vm.onToggleSave(target.id)
        advanceUntilIdle()
        assertThat(vm.uiState.value.savedIds).contains(target.id)
    }
}
