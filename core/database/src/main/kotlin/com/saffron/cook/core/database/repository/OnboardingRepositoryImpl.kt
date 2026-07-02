package com.saffron.cook.core.database.repository

import android.content.Context

class OnboardingRepositoryImpl(context: Context) : OnboardingRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun hasCompletedOnboarding(): Boolean = prefs.getBoolean(KEY_COMPLETED, false)

    override fun setOnboardingCompleted() {
        prefs.edit().putBoolean(KEY_COMPLETED, true).apply()
    }

    companion object {
        private const val PREFS_NAME = "onboarding_prefs"
        private const val KEY_COMPLETED = "has_completed_onboarding"
    }
}
