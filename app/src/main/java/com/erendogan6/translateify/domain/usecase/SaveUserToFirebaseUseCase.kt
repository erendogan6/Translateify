package com.erendogan6.translateify.domain.usecase

import com.erendogan6.translateify.domain.repository.WordRepository

class SaveUserToFirebaseUseCase(
    private val wordRepository: WordRepository,
) {
    suspend operator fun invoke(
        email: String,
        name: String,
        level: String,
        interests: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        try {
            wordRepository.saveUserToFirebase(email, name, level, interests)
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}
