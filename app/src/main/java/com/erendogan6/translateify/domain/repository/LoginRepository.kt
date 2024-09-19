package com.erendogan6.translateify.domain.repository

fun interface LoginRepository {
    suspend fun signInUser(
        email: String,
        password: String,
    ): Result<Unit>
}
