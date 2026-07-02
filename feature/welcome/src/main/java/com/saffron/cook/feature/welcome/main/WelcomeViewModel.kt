package com.saffron.cook.feature.welcome.main

import androidx.lifecycle.ViewModel
import com.saffron.cook.core.database.repository.OnboardingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WelcomeViewModel(
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    fun next() {
        _uiState.update { it.copy(currentSlide = (it.currentSlide + 1).coerceAtMost(WelcomeUiState.SLIDE_COUNT - 1)) }
    }

    fun goTo(index: Int) {
        _uiState.update { it.copy(currentSlide = index.coerceIn(0, WelcomeUiState.SLIDE_COUNT - 1)) }
    }

    fun skip() {
        _uiState.update { it.copy(currentSlide = WelcomeUiState.SLIDE_COUNT - 1) }
    }

    fun completeOnboarding() {
        onboardingRepository.setOnboardingCompleted()
    }
}
