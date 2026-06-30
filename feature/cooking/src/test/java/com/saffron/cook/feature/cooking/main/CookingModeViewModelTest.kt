package com.saffron.cook.feature.cooking.main

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.saffron.cook.core.data.repository.fake.FakeRecipeRepository
import com.saffron.cook.core.testing.FakeCookedRecipesRepository
import com.saffron.cook.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CookingModeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val cooked = FakeCookedRecipesRepository()

    private fun viewModel(
        repo: FakeRecipeRepository = FakeRecipeRepository(),
        recipeId: String = "1",
    ) = CookingModeViewModel(
        savedStateHandle = SavedStateHandle(mapOf("recipeId" to recipeId)),
        repository = repo,
        cookedRepository = cooked,
    )

    @Test
    fun `loads recipe on init`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        val state = vm.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.recipe?.id).isEqualTo("1")
    }

    @Test
    fun `load failure sets error`() = runTest {
        val vm = viewModel(repo = FakeRecipeRepository(shouldThrow = true))
        advanceUntilIdle()
        assertThat(vm.uiState.value.isError).isTrue()
    }

    @Test
    fun `onNext advances step and clears timer`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onShowTimer(seconds = 60, stepTitle = "Boil")
        vm.onNext()
        val state = vm.uiState.value
        assertThat(state.currentStepIndex).isEqualTo(1)
        assertThat(state.timerTotalSeconds).isNull()
        assertThat(state.showTimerDialog).isFalse()
    }

    @Test
    fun `toggle step done adds then removes current index`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onToggleStepDone()
        assertThat(vm.uiState.value.completedSteps).contains(0)
        vm.onToggleStepDone()
        assertThat(vm.uiState.value.completedSteps).doesNotContain(0)
    }

    @Test
    fun `timer counts down and emits finished event`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.timerFinishedEvent.test {
            vm.onShowTimer(seconds = 3, stepTitle = "Rest")
            advanceTimeBy(3_100)
            awaitItem() // finished event
            cancelAndIgnoreRemainingEvents()
        }
        assertThat(vm.uiState.value.timerRemainingSeconds).isEqualTo(0)
        assertThat(vm.uiState.value.isTimerRunning).isFalse()
    }

    @Test
    fun `pause stops the countdown`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onShowTimer(seconds = 10, stepTitle = "Rest")
        advanceTimeBy(2_100)
        vm.onPauseTimer()
        val remaining = vm.uiState.value.timerRemainingSeconds
        advanceTimeBy(5_000)
        assertThat(vm.uiState.value.timerRemainingSeconds).isEqualTo(remaining)
        assertThat(vm.uiState.value.isTimerRunning).isFalse()
    }

    @Test
    fun `add minute extends remaining and total`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onShowTimer(seconds = 60, stepTitle = "Rest")
        vm.onPauseTimer()
        vm.onAddMinute()
        assertThat(vm.uiState.value.timerTotalSeconds).isEqualTo(120)
        assertThat(vm.uiState.value.timerRemainingSeconds).isAtLeast(60)
    }

    @Test
    fun `re-opening same timer does not reset it`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onShowTimer(seconds = 30, stepTitle = "Rest")
        advanceTimeBy(5_100)
        vm.onDismissTimerDialog()
        val remaining = vm.uiState.value.timerRemainingSeconds
        vm.onShowTimer(seconds = 30, stepTitle = "Rest")
        assertThat(vm.uiState.value.showTimerDialog).isTrue()
        assertThat(vm.uiState.value.timerRemainingSeconds).isEqualTo(remaining)
    }

    @Test
    fun `finish marks finished and records cooked`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.onFinish()
        advanceUntilIdle()
        assertThat(vm.uiState.value.isFinished).isTrue()
        val items = cooked.allCookedFlow.first()
        assertThat(items).hasSize(1)
        assertThat(items.first().recipeId).isEqualTo("1")
    }
}
