package com.saffron.cook.ui.cooking

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saffron.cook.R
import com.saffron.cook.core.domain.model.Difficulty
import com.saffron.cook.core.domain.model.Ingredient
import com.saffron.cook.core.domain.model.Recipe
import com.saffron.cook.core.domain.model.Step
import com.saffron.cook.core.designsystem.theme.Cinnamon
import com.saffron.cook.core.designsystem.theme.Cream
import com.saffron.cook.core.designsystem.theme.PlayfairDisplayFamily
import com.saffron.cook.core.designsystem.theme.Saffron
import com.saffron.cook.core.designsystem.theme.Saffron160
import com.saffron.cook.core.designsystem.theme.SaffronTheme
import com.saffron.cook.core.designsystem.theme.Truffle
import org.koin.androidx.compose.koinViewModel

@Composable
fun CookingModeScreen(
    onBack: () -> Unit,
    onAddNote: (recipeId: String) -> Unit,
    viewModel: CookingModeViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.timerFinishedEvent.collect {
            val vibrator = context.getSystemService(Vibrator::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 400, 100, 400), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 400, 100, 400), -1)
            }
        }
    }

    CookingModeContent(
        state = state,
        onBack = onBack,
        onAddNote = onAddNote,
        onRetry = viewModel::retry,
        onSelectStep = viewModel::onSelectStep,
        onToggleStepDone = viewModel::onToggleStepDone,
        onNext = viewModel::onNext,
        onPrevious = viewModel::onPrevious,
        onFinish = viewModel::onFinish,
        onShowTimer = viewModel::onShowTimer,
        onDismissTimerDialog = viewModel::onDismissTimerDialog,
        onStartTimer = viewModel::onStartTimer,
        onPauseTimer = viewModel::onPauseTimer,
        onResetTimer = viewModel::onResetTimer,
        onAddMinute = viewModel::onAddMinute,
        onTimerDone = viewModel::onTimerDone,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CookingModeContent(
    state: CookingModeUiState,
    onBack: () -> Unit,
    onAddNote: (recipeId: String) -> Unit,
    onRetry: () -> Unit,
    onSelectStep: (Int) -> Unit,
    onToggleStepDone: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onFinish: () -> Unit,
    onShowTimer: (Int, String) -> Unit,
    onDismissTimerDialog: () -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onAddMinute: () -> Unit,
    onTimerDone: () -> Unit,
) {
    when {
        state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator(color = Saffron, strokeWidth = 2.dp)
        }
        state.isError -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.error_load_failed),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Cinnamon,
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                ) {
                    Text(stringResource(R.string.error_retry), color = Color.White)
                }
            }
        }
        state.recipe == null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(
                text = stringResource(R.string.recipe_not_found),
                style = MaterialTheme.typography.bodyLarge,
                color = Cinnamon,
            )
        }
        else -> {
            CookingLayout(
                state = state,
                recipe = state.recipe,
                onBack = onBack,
                onFinish = onFinish,
                onSelectStep = onSelectStep,
                onToggleStepDone = onToggleStepDone,
                onNext = onNext,
                onPrevious = onPrevious,
                onShowTimer = onShowTimer,
            )
            if (state.isFinished) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = onBack,
                    sheetState = sheetState,
                    containerColor = Color.White,
                ) {
                    CompletionSheetContent(
                        recipeName = state.recipe.title,
                        onClose = onBack,
                        onAddNote = { onAddNote(state.recipe.id) },
                        onMaybeLater = onBack,
                    )
                }
            }
            if (state.showTimerDialog) {
                val timerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = onDismissTimerDialog,
                    sheetState = timerSheetState,
                    containerColor = Color.White,
                ) {
                    TimerSheetContent(
                        totalSeconds = state.timerTotalSeconds ?: 0,
                        remainingSeconds = state.timerRemainingSeconds ?: 0,
                        stepTitle = state.timerStepTitle,
                        isRunning = state.isTimerRunning,
                        isFinished = state.timerFinished,
                        onClose = onDismissTimerDialog,
                        onStart = onStartTimer,
                        onPause = onPauseTimer,
                        onReset = onResetTimer,
                        onAddMinute = onAddMinute,
                        onDone = onTimerDone,
                    )
                }
            }
        }
    }
}

// ---- Completion sheet -------------------------------------------------------

@Composable
private fun CompletionSheetContent(
    recipeName: String,
    onClose: () -> Unit,
    onAddNote: () -> Unit,
    onMaybeLater: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 4.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.cd_exit),
                    tint = Truffle,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Cream),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = Saffron,
                    modifier = Modifier.size(30.dp),
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = recipeName.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Saffron,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.cooking_done_headline),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = PlayfairDisplayFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 30.sp,
                    lineHeight = 35.4.sp,
                    letterSpacing = (-0.3).sp,
                ),
                color = Truffle,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.cooking_done_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = Cinnamon,
            )
        }

        Spacer(Modifier.height(28.dp))

        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE4DFD5))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = onAddNote,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                elevation = ButtonDefaults.buttonElevation(0.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.cooking_add_note),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                )
            }

            Button(
                onClick = onMaybeLater,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Truffle,
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
                Text(
                    text = stringResource(R.string.cooking_maybe_later),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

// ---- Timer sheet ------------------------------------------------------------

@Composable
private fun TimerSheetContent(
    totalSeconds: Int,
    remainingSeconds: Int,
    stepTitle: String?,
    isRunning: Boolean,
    isFinished: Boolean,
    onClose: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onAddMinute: () -> Unit,
    onDone: () -> Unit,
) {
    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 0f
    val arcColor = if (isFinished) Saffron160 else Saffron

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (isFinished) {
                    stringResource(R.string.cooking_timer_finished)
                } else {
                    stringResource(R.string.cooking_timer_title)
                },
                style = MaterialTheme.typography.labelMedium,
                color = Saffron,
            )
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.cd_close_timer),
                    tint = Truffle,
                )
            }
        }

        // "From step: …"
        if (stepTitle != null) {
            Text(
                text = stringResource(R.string.cooking_timer_from_step, stepTitle),
                style = MaterialTheme.typography.labelSmall,
                color = Cinnamon,
            )
            Spacer(Modifier.height(10.dp))
        } else {
            Spacer(Modifier.height(6.dp))
        }

        // Progress ring with time + status caption
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(220.dp),
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(220.dp),
                strokeWidth = 10.dp,
                strokeCap = StrokeCap.Round,
                color = arcColor,
                trackColor = Cream,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = formatSeconds(remainingSeconds),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = PlayfairDisplayFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 48.sp,
                        lineHeight = 52.sp,
                    ),
                    textAlign = TextAlign.Center,
                    color = Truffle,
                )
                Text(
                    text = when {
                        isFinished -> stringResource(R.string.cooking_timer_done_label)
                        isRunning -> stringResource(R.string.cooking_timer_counting_down)
                        else -> stringResource(R.string.cooking_timer_paused_label)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = Cinnamon,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE4DFD5))

        // Button row: [↺] [Pause/Resume or Done] [+]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = stringResource(R.string.cooking_timer_reset),
                    tint = Truffle,
                )
            }

            if (isFinished) {
                Button(
                    onClick = onDone,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.cooking_timer_done),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                    )
                }
            } else if (isRunning) {
                Button(
                    onClick = onPause,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                ) {
                    Text(
                        text = stringResource(R.string.cooking_timer_pause),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                    )
                }
            } else {
                Button(
                    onClick = onStart,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                ) {
                    Text(
                        text = stringResource(R.string.cooking_timer_resume),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                    )
                }
            }

            IconButton(onClick = onAddMinute) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cooking_timer_add_minute),
                    tint = Truffle,
                )
            }
        }
    }
}

// ---- Cooking layout ---------------------------------------------------------

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
    onShowTimer: (Int, String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 14.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.cd_exit),
                    tint = Truffle,
                )
            }
            Text(
                text = stringResource(R.string.cooking_step_progress, state.currentStepIndex + 1, state.totalSteps),
                style = MaterialTheme.typography.labelLarge,
                color = Cinnamon,
            )
            Spacer(Modifier.size(48.dp))
        }

        if (state.steps.isNotEmpty()) {
            StepIndicatorRow(
                totalSteps = state.totalSteps,
                currentIndex = state.currentStepIndex,
                completedSteps = state.completedSteps,
                onSelectStep = onSelectStep,
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (state.steps.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(
                        text = stringResource(R.string.cooking_no_steps),
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
                            step = step,
                            recipeName = recipe.title,
                            isDone = stepIndex in state.completedSteps,
                            onToggleDone = onToggleStepDone,
                            onShowTimer = { seconds -> onShowTimer(seconds, step.title) },
                        )
                    }
                }
            }
        }

        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE4DFD5))
        Footer(
            isFirstStep = state.isFirstStep,
            isLastStep = state.isLastStep,
            onPrevious = onPrevious,
            onNext = onNext,
            onFinish = onFinish,
        )
    }
}

// ---- Step indicator ---------------------------------------------------------

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
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 14.dp),
    ) {
        items(count = totalSteps) { index ->
            StepPill(
                number = index + 1,
                isActive = index == currentIndex,
                isCompleted = index in completedSteps,
                onClick = { onSelectStep(index) },
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
    val bg = if (isActive) Saffron else Cream
    val textColor = when {
        isActive -> Color.White
        isCompleted -> Saffron160
        else -> Cinnamon
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
            text = number.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
        )
    }
}

// ---- Step content -----------------------------------------------------------

@Composable
private fun StepContent(
    step: Step,
    recipeName: String,
    isDone: Boolean,
    onToggleDone: () -> Unit,
    onShowTimer: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val timedSpans = remember(step.instruction) { parseTimedSpans(step.instruction) }

    val annotated = remember(step.instruction, timedSpans) {
        buildAnnotatedString {
            var cursor = 0
            timedSpans.forEach { span ->
                append(step.instruction.substring(cursor, span.start))
                val seconds = span.seconds
                pushLink(
                    LinkAnnotation.Clickable(tag = "TIMER") { onShowTimer(seconds) },
                )
                withStyle(
                    SpanStyle(
                        color = Saffron160,
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.Underline,
                    ),
                ) {
                    append(step.instruction.substring(span.start, span.end))
                }
                pop()
                cursor = span.end
            }
            append(step.instruction.substring(cursor))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Text(
            text = recipeName.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Saffron,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = step.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = PlayfairDisplayFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 30.sp,
                lineHeight = 35.4.sp,
                letterSpacing = (-0.3).sp,
            ),
            color = Truffle,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = annotated,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 17.sp,
                lineHeight = 29.75.sp,
            ),
            color = Truffle,
        )
        if (timedSpans.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                timedSpans.forEach { span ->
                    TimerChip(
                        seconds = span.seconds,
                        onClick = { onShowTimer(span.seconds) },
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.clickable(onClick = onToggleDone),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isDone,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = Saffron,
                    uncheckedColor = Cinnamon,
                    checkmarkColor = Color.White,
                ),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.cooking_mark_done),
                style = MaterialTheme.typography.labelLarge,
                color = if (isDone) Saffron else Truffle,
            )
        }
    }
}

// ---- Timer chip -------------------------------------------------------------

@Composable
private fun TimerChip(seconds: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(percent = 50),
        color = Cream,
        border = BorderStroke(0.5.dp, Color(0xFFD3CFC8)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer,
                contentDescription = null,
                tint = Saffron160,
                modifier = Modifier.size(15.dp),
            )
            Text(
                text = stringResource(R.string.cooking_timer_set, formatSeconds(seconds)),
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp),
                color = Saffron160,
            )
        }
    }
}

// ---- Footer -----------------------------------------------------------------

@Composable
private fun Footer(
    isFirstStep: Boolean,
    isLastStep: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = onPrevious,
            enabled = !isFirstStep,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Truffle,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color(0xFFD3CFC8),
            ),
            border = BorderStroke(0.5.dp, if (isFirstStep) Color(0xFFE4DFD5) else Color(0xFFD3CFC8)),
            elevation = ButtonDefaults.buttonElevation(0.dp),
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.cooking_previous),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        if (isLastStep) {
            Button(
                onClick = onFinish,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.cooking_finish),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                )
            }
        } else {
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Saffron),
                elevation = ButtonDefaults.buttonElevation(0.dp),
            ) {
                Text(
                    text = stringResource(R.string.cooking_next),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

// ---- Formatting helpers -----------------------------------------------------

private fun formatSeconds(s: Int): String {
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, sec) else "%d:%02d".format(m, sec)
}

// ---- Previews ---------------------------------------------------------------

private val previewCookingRecipe = Recipe(
    id = "52772",
    title = "Teriyaki Chicken Casserole",
    description = "",
    imageUrl = "",
    categoryId = "chicken",
    ingredients = listOf(Ingredient("3/4 cup", "soy sauce")),
    cookTimeMinutes = 35,
    servings = 4,
    difficulty = Difficulty.Medium,
    steps = listOf(
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
                isLoading = false,
                recipe = previewCookingRecipe,
                currentStepIndex = 2,
                completedSteps = setOf(0, 1),
            ),
            recipe = previewCookingRecipe,
            onBack = {},
            onFinish = {},
            onSelectStep = {},
            onToggleStepDone = {},
            onNext = {},
            onPrevious = {},
            onShowTimer = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerSheetRunningPreview() {
    SaffronTheme {
        TimerSheetContent(
            totalSeconds = 1500,
            remainingSeconds = 847,
            stepTitle = "Bake",
            isRunning = true,
            isFinished = false,
            onClose = {},
            onStart = {},
            onPause = {},
            onReset = {},
            onAddMinute = {},
            onDone = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerSheetFinishedPreview() {
    SaffronTheme {
        TimerSheetContent(
            totalSeconds = 600,
            remainingSeconds = 0,
            stepTitle = "Bake",
            isRunning = false,
            isFinished = true,
            onClose = {},
            onStart = {},
            onPause = {},
            onReset = {},
            onAddMinute = {},
            onDone = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompletionSheetPreview() {
    SaffronTheme {
        CompletionSheetContent(
            recipeName = "Teriyaki Chicken Casserole",
            onClose = {},
            onAddNote = {},
            onMaybeLater = {},
        )
    }
}
