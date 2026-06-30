package com.saffron.cook.feature.cooking.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saffron.cook.core.domain.repository.RecipeRepository
import com.saffron.cook.core.database.repository.CookedRecipesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimedSpan(val start: Int, val end: Int, val seconds: Int)

private val TIME_REGEX = Regex(
    """(\d+)\s*[-–]\s*(\d+)\s*(min(?:ute)?s?|sec(?:ond)?s?|h(?:ou)?rs?)""" +
        """|(\d+(?:\.\d+)?)\s*(min(?:ute)?s?|sec(?:ond)?s?|h(?:ou)?rs?)""",
    RegexOption.IGNORE_CASE,
)

fun parseTimedSpans(text: String): List<TimedSpan> {
    return TIME_REGEX.findAll(text).map { match ->
        val seconds = if (match.groupValues[1].isNotEmpty()) {
            val upper = match.groupValues[2].toDouble()
            val unit = match.groupValues[3]
            toSeconds(upper, unit)
        } else {
            val value = match.groupValues[4].toDouble()
            val unit = match.groupValues[5]
            toSeconds(value, unit)
        }
        TimedSpan(match.range.first, match.range.last + 1, seconds)
    }.filter { it.seconds > 0 }.toList()
}

private fun toSeconds(value: Double, unit: String): Int {
    val lower = unit.lowercase()
    return when {
        lower.startsWith("h") -> (value * 3600).toInt()
        lower.startsWith("m") -> (value * 60).toInt()
        else -> value.toInt()
    }
}

class CookingModeViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: RecipeRepository,
    private val cookedRepository: CookedRecipesRepository,
) : ViewModel() {

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"])

    private val _uiState = MutableStateFlow(CookingModeUiState())
    val uiState: StateFlow<CookingModeUiState> = _uiState.asStateFlow()

    private val _timerFinishedEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val timerFinishedEvent: SharedFlow<Unit> = _timerFinishedEvent.asSharedFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch { load() }
    }

    private suspend fun load() {
        _uiState.update { it.copy(isLoading = true, isError = false) }
        try {
            val recipe = repository.getRecipeById(recipeId)
            _uiState.update { it.copy(isLoading = false, recipe = recipe) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, isError = true) }
        }
    }

    fun retry() {
        viewModelScope.launch { load() }
    }

    fun onSelectStep(index: Int) {
        cancelAndResetTimer()
        _uiState.update { it.copy(currentStepIndex = index) }
    }

    fun onToggleStepDone() {
        _uiState.update { state ->
            val completed = state.completedSteps.toMutableSet()
            if (state.currentStepIndex in completed) completed.remove(state.currentStepIndex)
            else completed.add(state.currentStepIndex)
            state.copy(completedSteps = completed)
        }
    }

    fun onNext() {
        cancelAndResetTimer()
        _uiState.update { state ->
            if (!state.isLastStep) state.copy(currentStepIndex = state.currentStepIndex + 1)
            else state
        }
    }

    fun onPrevious() {
        cancelAndResetTimer()
        _uiState.update { state ->
            if (!state.isFirstStep) state.copy(currentStepIndex = state.currentStepIndex - 1)
            else state
        }
    }

    fun onFinish() {
        _uiState.update { it.copy(isFinished = true) }
        _uiState.value.recipe?.let { recipe ->
            viewModelScope.launch { cookedRepository.recordCooked(recipe) }
        }
    }

    fun onShowTimer(seconds: Int, stepTitle: String) {
        val current = _uiState.value
        // Re-open existing timer for the same step without resetting it
        val alreadyConfigured = current.timerStepTitle == stepTitle &&
            current.timerInitialSeconds == seconds &&
            current.timerTotalSeconds != null
        if (alreadyConfigured) {
            _uiState.update { it.copy(showTimerDialog = true) }
            return
        }
        // New timer — configure and auto-start
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                timerInitialSeconds = seconds,
                timerTotalSeconds = seconds,
                timerRemainingSeconds = seconds,
                timerStepTitle = stepTitle,
                isTimerRunning = false,
                showTimerDialog = true,
            )
        }
        onStartTimer()
    }

    fun onDismissTimerDialog() {
        _uiState.update { it.copy(showTimerDialog = false) }
    }

    fun onStartTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _uiState.update { it.copy(isTimerRunning = true) }
            while ((_uiState.value.timerRemainingSeconds ?: 0) > 0) {
                delay(1000)
                _uiState.update { it.copy(timerRemainingSeconds = (it.timerRemainingSeconds ?: 0) - 1) }
            }
            _uiState.update { it.copy(isTimerRunning = false) }
            _timerFinishedEvent.emit(Unit)
        }
    }

    fun onPauseTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update { it.copy(isTimerRunning = false) }
    }

    fun onResetTimer() {
        timerJob?.cancel()
        timerJob = null
        val initial = _uiState.value.timerInitialSeconds ?: return
        _uiState.update { state ->
            state.copy(
                timerRemainingSeconds = initial,
                timerTotalSeconds = initial,
                isTimerRunning = false,
            )
        }
        onStartTimer()
    }

    fun onAddMinute() {
        _uiState.update { state ->
            state.copy(
                timerRemainingSeconds = (state.timerRemainingSeconds ?: 0) + 60,
                timerTotalSeconds = (state.timerTotalSeconds ?: 0) + 60,
            )
        }
    }

    fun onTimerDone() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update {
            it.copy(
                timerInitialSeconds = null,
                timerTotalSeconds = null,
                timerRemainingSeconds = null,
                timerStepTitle = null,
                isTimerRunning = false,
                showTimerDialog = false,
            )
        }
    }

    private fun cancelAndResetTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update {
            it.copy(
                timerInitialSeconds = null,
                timerTotalSeconds = null,
                timerRemainingSeconds = null,
                timerStepTitle = null,
                isTimerRunning = false,
                showTimerDialog = false,
            )
        }
    }
}
