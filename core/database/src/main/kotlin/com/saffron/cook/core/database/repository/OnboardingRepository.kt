package com.saffron.cook.core.database.repository

interface OnboardingRepository {
    fun hasCompletedOnboarding(): Boolean
    fun setOnboardingCompleted()
}
