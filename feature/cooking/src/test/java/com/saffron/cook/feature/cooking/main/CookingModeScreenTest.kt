package com.saffron.cook.feature.cooking.main

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.model.Step
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class CookingModeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val recipe = Recipe(
        id = "1",
        title = "Cacio e Pepe",
        description = "",
        imageUrl = "",
        categoryId = "italian",
        ingredients = emptyList(),
        steps = listOf(
            Step("Toast the pepper", "Toast until fragrant."),
            Step("Cook the pasta", "Boil for eight minutes."),
        ),
    )

    @Test
    fun `renders step title and progress on first step`() {
        CookingModeRobot(composeRule)
            .setContent(CookingModeUiState(isLoading = false, recipe = recipe))
            .assertTextDisplayed("Toast the pepper")
            .assertStepProgressVisible(current = 1, total = 2)
    }

    @Test
    fun `shows next step button when not on last step`() {
        CookingModeRobot(composeRule)
            .setContent(CookingModeUiState(isLoading = false, recipe = recipe, currentStepIndex = 0))
            .assertTextDisplayed("Next step")
    }

    @Test
    fun `shows finish button on last step`() {
        CookingModeRobot(composeRule)
            .setContent(CookingModeUiState(isLoading = false, recipe = recipe, currentStepIndex = 1))
            .assertTextDisplayed("Cook the pasta")
            .assertTextDisplayed("Finish")
    }
}
