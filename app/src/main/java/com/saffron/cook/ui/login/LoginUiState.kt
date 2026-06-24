package com.saffron.cook.ui.login

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface LoginEvent {
    data object SignedIn : LoginEvent
}
