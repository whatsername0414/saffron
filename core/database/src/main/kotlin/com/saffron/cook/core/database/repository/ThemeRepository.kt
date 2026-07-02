package com.saffron.cook.core.database.repository

import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {
    val themeMode: StateFlow<ThemeMode>
    fun setThemeMode(mode: ThemeMode)
}
