package com.saffron.cook.feature.profile.main

import com.google.firebase.auth.FirebaseUser
import com.saffron.cook.core.database.repository.ThemeMode

data class ProfileUiState(
    val user: FirebaseUser? = null,
    val savedCount: Int = 0,
    val cookedCount: Int = 0,
    val notesCount: Int = 0,
    val isSigningIn: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.System,
)
