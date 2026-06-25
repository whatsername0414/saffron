package com.saffron.cook.ui.profile

import com.google.firebase.auth.FirebaseUser

data class ProfileUiState(
    val user: FirebaseUser? = null,
    val savedCount: Int = 0,
    val cookedCount: Int = 0,
    val notesCount: Int = 0,
    val isSigningIn: Boolean = false,
)
