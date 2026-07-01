package com.saffron.cook.feature.cooking.main

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.saffron.cook.core.designsystem.theme.SaffronTheme

class CookingModeRobot(private val rule: ComposeContentTestRule) {

    fun setContent(state: CookingModeUiState) = apply {
        rule.setContent {
            SaffronTheme {
                CookingModeContent(
                    state = state,
                    onBack = {},
                    onAddNote = {},
                    onRetry = {},
                    onSelectStep = {},
                    onToggleStepDone = {},
                    onNext = {},
                    onPrevious = {},
                    onFinish = {},
                    onShowTimer = { _, _ -> },
                    onDismissTimerDialog = {},
                    onStartTimer = {},
                    onPauseTimer = {},
                    onResetTimer = {},
                    onAddMinute = {},
                    onTimerDone = {},
                )
            }
        }
    }

    fun assertTextDisplayed(text: String) = apply {
        rule.onNodeWithText(text, substring = true).assertIsDisplayed()
    }

    fun tapMarkDone() = apply {
        rule.onNodeWithText("Mark this step done", substring = true).performClick()
    }

    fun tapNext() = apply {
        rule.onNodeWithText("Next step", substring = true).performClick()
    }

    fun assertStepProgressVisible(current: Int, total: Int) = apply {
        rule.onNodeWithText("Step $current of $total", substring = true).assertIsDisplayed()
    }
}
