package com.saffron.cook.ui.cooking

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saffron.cook.R
import com.saffron.cook.core.data.model.Difficulty
import com.saffron.cook.core.data.model.Ingredient
import com.saffron.cook.core.data.model.Recipe
import com.saffron.cook.core.data.model.Step
import com.saffron.cook.ui.theme.Cinnamon
import com.saffron.cook.ui.theme.Cream
import com.saffron.cook.ui.theme.PlayfairDisplayFamily
import com.saffron.cook.ui.theme.Saffron
import com.saffron.cook.ui.theme.Saffron160
import com.saffron.cook.ui.theme.SaffronTheme
import com.saffron.cook.ui.theme.Truffle
import org.koin.androidx.compose.koinViewModel

@Composable
fun CookingModeScreen(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    viewModel: CookingModeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    CookingModeContent(
        state            = state,
        onBack           = onBack,
        onFinish         = onFinish,
        onRetry          = viewModel::retry,
        onSelectStep     = viewModel::onSelectStep,
        onToggleStepDone = viewModel::onToggleStepDone,
        onNext           = viewModel::onNext,
        onPrevious       = viewModel::onPrevious,
    )
}

@Composable
private fun CookingModeContent(
    state: CookingModeUiState,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    onRetry: () -> Unit,
    onSelectStep: (Int) -> Unit,
    onToggleStepDone: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator(color = Saffron, strokeWidth = 2.dp)
        }
        state.isError -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text  = stringResource(R.string.error_load_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Cinnamon,
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onRetry,
                    colors  = ButtonDefaults.buttonColors(containerColor = Saffron),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                ) {
                    Text(stringResource(R.string.error_retry), color = Color.White)
                }
            }
        }
        state.recipe == null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(
                text  = stringResource(R.string.recipe_not_found),
                style = MaterialTheme.typography.bodyLarge,
                color = Cinnamon,
            )
        }
        else -> CookingLayout(
            state            = state,
            recipe           = state.recipe,
            onBack           = onBack,
            onFinish         = onFinish,
            onSelectStep     = onSelectStep,
            onToggleStepDone = onToggleStepDone,
            onNext           = onNext,
            onPrevious       = onPrevious,
        )
    }
}

@Composable
private fun CookingLayout(
    state: CookingModeUiState,
    recipe: Recipe,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    onSelectStep: (Int) -> Unit,
    onToggleStepDone: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        // Header: exit × | Step N of M | spacer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 14.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector        = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.cd_exit),
                    tint               = Truffle,
                )
            }
            Text(
                text  = stringResource(R.string.cooking_step_progress, state.currentStepIndex + 1, state.totalSteps),
                style = MaterialTheme.typography.labelLarge,
                color = Cinnamon,
            )
            Spacer(Modifier.size(48.dp))
        }

        if (state.steps.isNotEmpty()) {
            StepIndicatorRow(
                totalSteps     = state.totalSteps,
                currentIndex   = state.currentStepIndex,
                completedSteps = state.completedSteps,
                onSelectStep   = onSelectStep,
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (state.steps.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(
                        text  = stringResource(R.string.cooking_no_steps),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Cinnamon,
                    )
                }
            } else {
                AnimatedContent(
                    targetState = state.currentStepIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                        } else {
                            slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                        }
                    },
                    label = "step_slide",
                ) { stepIndex ->
                    state.steps.getOrNull(stepIndex)?.let { step ->
                        StepContent(
                            step         = step,
                            recipeName   = recipe.title,
                            isDone       = stepIndex in state.completedSteps,
                            onToggleDone = onToggleStepDone,
                        )
                    }
                }
            }
        }

        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE4DFD5))
        Footer(
            isFirstStep = state.isFirstStep,
            isLastStep  = state.isLastStep,
            onPrevious  = onPrevious,
            onNext      = onNext,
            onFinish    = onFinish,
        )
    }
}

// ---- Step indicator --------------------------------------------------------

@Composable
private fun StepIndicatorRow(
    totalSteps: Int,
    currentIndex: Int,
    completedSteps: Set<Int>,
    onSelectStep: (Int) -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(currentIndex) {
        listState.animateScrollToItem(currentIndex)
    }
    LazyRow(
        state                 = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding        = PaddingValues(start = 16.dp, end = 16.dp, bottom = 14.dp),
    ) {
        items(count = totalSteps) { index ->
            StepPill(
                number      = index + 1,
                isActive    = index == currentIndex,
                isCompleted = index in completedSteps,
                onClick     = { onSelectStep(index) },
            )
        }
    }
}

@Composable
private fun StepPill(
    number: Int,
    isActive: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit,
) {
    val bg        = if (isActive) Saffron else Cream
    val textColor = when {
        isActive    -> Color.White
        isCompleted -> Saffron160
        else        -> Cinnamon
    }

    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 36.dp)
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = number.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
        )
    }
}

// ---- Step content ----------------------------------------------------------

@Composable
private fun StepContent(
    step: Step,
    recipeName: String,
    isDone: Boolean,
    onToggleDone: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Text(
            text  = recipeName.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Saffron,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text  = step.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily    = PlayfairDisplayFamily,
                fontWeight    = FontWeight.Normal,
                fontSize      = 30.sp,
                lineHeight    = 35.4.sp,
                letterSpacing = (-0.3).sp,
            ),
            color = Truffle,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text  = step.instruction,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize   = 17.sp,
                lineHeight = 29.75.sp,
            ),
            color = Truffle,
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier          = Modifier.clickable(onClick = onToggleDone),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked         = isDone,
                onCheckedChange = null,
                colors          = CheckboxDefaults.colors(
                    checkedColor   = Saffron,
                    uncheckedColor = Cinnamon,
                    checkmarkColor = Color.White,
                ),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text  = stringResource(R.string.cooking_mark_done),
                style = MaterialTheme.typography.labelLarge,
                color = if (isDone) Saffron else Truffle,
            )
        }
    }
}

// ---- Footer ----------------------------------------------------------------

@Composable
private fun Footer(
    isFirstStep: Boolean,
    isLastStep: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit,
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        // Back — natural width
        Button(
            onClick   = onPrevious,
            enabled   = !isFirstStep,
            shape     = RoundedCornerShape(10.dp),
            colors    = ButtonDefaults.buttonColors(
                containerColor         = Color.Transparent,
                contentColor           = Truffle,
                disabledContainerColor = Color.Transparent,
                disabledContentColor   = Color(0xFFD3CFC8),
            ),
            border    = BorderStroke(0.5.dp, if (isFirstStep) Color(0xFFE4DFD5) else Color(0xFFD3CFC8)),
            elevation = ButtonDefaults.buttonElevation(0.dp),
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                text  = stringResource(R.string.cooking_previous),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        // Next step or Finish — fills remaining space
        if (isLastStep) {
            Button(
                onClick   = onFinish,
                modifier  = Modifier.weight(1f),
                shape     = RoundedCornerShape(10.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = Saffron),
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
                Icon(
                    imageVector        = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text  = stringResource(R.string.cooking_finish),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                )
            }
        } else {
            Button(
                onClick   = onNext,
                modifier  = Modifier.weight(1f),
                shape     = RoundedCornerShape(10.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = Saffron),
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
                Text(
                    text  = stringResource(R.string.cooking_next),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector        = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(18.dp),
                )
            }
        }
    }
}

// ---- Preview ---------------------------------------------------------------

private val previewCookingRecipe = Recipe(
    id              = "52772",
    title           = "Teriyaki Chicken Casserole",
    description     = "",
    imageUrl        = "",
    categoryId      = "chicken",
    ingredients     = listOf(Ingredient("3/4 cup", "soy sauce")),
    cookTimeMinutes = 35,
    servings        = 4,
    difficulty      = Difficulty.Medium,
    steps           = listOf(
        Step("Preheat the oven", "Preheat oven to 190 °C. Mix soy sauce, water, brown sugar, sesame oil, and garlic powder in a bowl until the sugar dissolves."),
        Step("Marinate the chicken", "Place chicken pieces in a single layer in a large baking dish. Pour the marinade over the chicken, turning each piece to coat well."),
        Step("Bake", "Cover the dish with foil and bake for 25 minutes. Remove the foil and bake for a further 10 minutes until the glaze is sticky and caramelised."),
        Step("Rest and serve", "Remove from oven and rest for 5 minutes. Serve over steamed jasmine rice with a sprinkle of sesame seeds and sliced spring onion."),
    ),
)

@Preview(showBackground = true)
@Composable
private fun CookingModePreview() {
    SaffronTheme {
        CookingLayout(
            state = CookingModeUiState(
                isLoading        = false,
                recipe           = previewCookingRecipe,
                currentStepIndex = 1,
                completedSteps   = setOf(0),
            ),
            recipe           = previewCookingRecipe,
            onBack           = {},
            onFinish         = {},
            onSelectStep     = {},
            onToggleStepDone = {},
            onNext           = {},
            onPrevious       = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CookingModeLastStepPreview() {
    SaffronTheme {
        CookingLayout(
            state = CookingModeUiState(
                isLoading        = false,
                recipe           = previewCookingRecipe,
                currentStepIndex = 3,
                completedSteps   = setOf(0, 1, 2, 3),
            ),
            recipe           = previewCookingRecipe,
            onBack           = {},
            onFinish         = {},
            onSelectStep     = {},
            onToggleStepDone = {},
            onNext           = {},
            onPrevious       = {},
        )
    }
}
