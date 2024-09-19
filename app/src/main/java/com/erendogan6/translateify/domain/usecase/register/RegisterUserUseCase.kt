package com.erendogan6.translateify.domain.usecase.register

import com.erendogan6.translateify.domain.repository.RegisterRepository
import javax.inject.Inject

class RegisterUserUseCase
    @Inject
    constructor(
        private val registerRepository: RegisterRepository,
    ) {
        suspend operator fun invoke(
            email: String,
            password: String,
        ) {
            registerRepository.createUserWithEmail(email, password)
        }
    }
