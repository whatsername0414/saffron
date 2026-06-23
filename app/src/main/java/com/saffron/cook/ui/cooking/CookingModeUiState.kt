package com.saffron.cook.ui.cooking

import com.saffron.cook.core.data.model.Recipe
import com.saffron.cook.core.data.model.Step

data class CookingModeUiState(
    val isLoading: Boolean = true,
    val recipe: Recipe? = null,
    val currentStepIndex: Int = 0,
    val completedSteps: Set<Int> = emptySet(),
    val isError: Boolean = false,
) {
    val steps: List<Step> get() = recipe?.steps.orEmpty()
    val totalSteps: Int get() = steps.size
    val isFirstStep: Boolean get() = currentStepIndex == 0
    val isLastStep: Boolean get() = totalSteps > 0 && currentStepIndex == totalSteps - 1
    val currentStepDone: Boolean get() = currentStepIndex in completedSteps
}
