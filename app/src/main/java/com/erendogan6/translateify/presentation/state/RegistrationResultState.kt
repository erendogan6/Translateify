package com.erendogan6.translateify.presentation.state

sealed class RegistrationResultState {
    data object Success : RegistrationResultState()

    data class Error(
        val message: String,
    ) : RegistrationResultState()

    data object Idle : RegistrationResultState()

    data object Loading : RegistrationResultState()
}
