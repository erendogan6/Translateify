package com.erendogan6.translateify.domain.usecase.login

import com.erendogan6.translateify.domain.repository.LoginRepository
import javax.inject.Inject

class LoginUseCase
    @Inject
    constructor(
        private val loginRepository: LoginRepository,
    ) {
        suspend operator fun invoke(
            email: String,
            password: String,
        ): Result<Unit> {
            if (email.isEmpty()) {
                return Result.failure(Exception("Email is required"))
            }

            if (password.isEmpty()) {
                return Result.failure(Exception("Password is required"))
            }

            return loginRepository.signInUser(email, password)
        }
    }
