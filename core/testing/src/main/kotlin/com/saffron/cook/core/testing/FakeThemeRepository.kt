package com.saffron.cook.core.testing

import com.saffron.cook.core.database.repository.ThemeMode
import com.saffron.cook.core.database.repository.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeThemeRepository(initial: ThemeMode = ThemeMode.System) : ThemeRepository {
    private val _themeMode = MutableStateFlow(initial)
    override val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    override fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
}
