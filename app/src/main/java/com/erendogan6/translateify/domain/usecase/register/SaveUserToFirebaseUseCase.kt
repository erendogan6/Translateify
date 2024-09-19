package com.erendogan6.translateify.domain.usecase.register

import com.erendogan6.translateify.domain.repository.RegisterRepository
import javax.inject.Inject

class SaveUserToFirebaseUseCase
    @Inject
    constructor(
        private val registerRepository: RegisterRepository,
    ) {
        suspend operator fun invoke(
            email: String,
            name: String,
            level: String,
            interests: List<String>,
        ) {
            registerRepository.saveUserToFirestore(email, name, level, interests)
        }
    }
