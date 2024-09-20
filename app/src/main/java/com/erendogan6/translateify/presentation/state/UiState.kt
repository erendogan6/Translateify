package com.erendogan6.translateify.presentation.state

import com.erendogan6.translateify.domain.model.Word

sealed class UiState {
    data object Idle : UiState()

    data object Loading : UiState()

    data class Success(
        val words: List<Word>,
    ) : UiState()

    data class Error(
        val message: String,
    ) : UiState()
}
