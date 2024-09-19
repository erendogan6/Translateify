package com.erendogan6.translateify.domain.repository

interface LoginRepository {
    suspend fun signInUser(
        email: String,
        password: String,
    ): Result<Unit>
}
