package com.saffron.cook.feature.welcome.main

data class WelcomeUiState(
    val currentSlide: Int = 0,
) {
    val isLastSlide: Boolean get() = currentSlide == SLIDE_COUNT - 1

    companion object {
        const val SLIDE_COUNT = 3
    }
}
