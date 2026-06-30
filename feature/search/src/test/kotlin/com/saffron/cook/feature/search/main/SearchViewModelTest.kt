package com.saffron.cook.feature.search.main

import com.google.common.truth.Truth.assertThat
import com.saffron.cook.core.data.repository.fake.FakeRecipeRepository
import com.saffron.cook.core.testing.FakeSavedRecipesRepository
import com.saffron.cook.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val saved = FakeSavedRecipesRepository()

    private fun viewModel(repo: FakeRecipeRepository = FakeRecipeRepository()) =
        SearchViewModel(repository = repo, savedRecipesRepository = saved)

    @Test
    fun `loads initial recipes on init`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        assertThat(vm.uiState.value.initialRecipes).isNotEmpty()
        assertThat(vm.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `blank query clears results without searching`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onQueryChange("")
        assertThat(vm.uiState.value.results).isEmpty()
    }

    @Test
    fun `query debounces before producing results`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onQueryChange("cacio")
        advanceTimeBy(200) // less than 300ms debounce
        assertThat(vm.uiState.value.results).isEmpty()
        advanceTimeBy(150) // crosses 300ms total
        advanceUntilIdle()
        assertThat(vm.uiState.value.results.map { it.title }).contains("Cacio e Pepe")
    }

    @Test
    fun `rapid typing cancels the prior search`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onQueryChange("cacio")
        advanceTimeBy(100)
        vm.onQueryChange("miso")
        advanceTimeBy(350)
        advanceUntilIdle()
        val titles = vm.uiState.value.results.map { it.title }
        assertThat(titles).contains("Miso Glazed Salmon")
        assertThat(titles).doesNotContain("Cacio e Pepe")
    }

    @Test
    fun `search error sets isError`() = runTest {
        val repo = FakeRecipeRepository()
        val vm = viewModel(repo)
        advanceUntilIdle()
        repo.shouldThrow = true
        vm.onQueryChange("cacio")
        advanceTimeBy(350)
        advanceUntilIdle()
        assertThat(vm.uiState.value.isError).isTrue()
    }

    @Test
    fun `toggle save reflects in savedIds`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        val first = vm.uiState.value.initialRecipes.first()
        vm.onToggleSave(first.id)
        advanceUntilIdle()
        assertThat(vm.uiState.value.savedIds).contains(first.id)
    }
}
