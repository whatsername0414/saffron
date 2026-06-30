package com.saffron.cook.feature.recipe.main

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.saffron.cook.core.data.repository.fake.FakeRecipeRepository
import com.saffron.cook.core.testing.FakeSavedRecipesRepository
import com.saffron.cook.core.testing.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class RecipeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val saved = FakeSavedRecipesRepository()

    private fun viewModel(repo: FakeRecipeRepository = FakeRecipeRepository(), recipeId: String = "1") =
        RecipeViewModel(
            savedStateHandle = SavedStateHandle(mapOf("recipeId" to recipeId)),
            repository = repo,
            savedRecipesRepository = saved,
        )

    @Test
    fun `loads recipe by id`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        assertThat(vm.uiState.value.recipe?.id).isEqualTo("1")
        assertThat(vm.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `load failure sets error then retry succeeds`() = runTest {
        val repo = FakeRecipeRepository(shouldThrow = true)
        val vm = viewModel(repo)
        advanceUntilIdle()
        assertThat(vm.uiState.value.isError).isTrue()
        repo.shouldThrow = false
        vm.retry()
        advanceUntilIdle()
        assertThat(vm.uiState.value.isError).isFalse()
        assertThat(vm.uiState.value.recipe?.id).isEqualTo("1")
    }

    @Test
    fun `toggle save flips isSaved`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        assertThat(vm.uiState.value.isSaved).isFalse()
        vm.onToggleSave()
        advanceUntilIdle()
        assertThat(vm.uiState.value.isSaved).isTrue()
    }
}
