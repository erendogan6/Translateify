package com.erendogan6.translateify.domain.repository

import com.google.firebase.auth.AuthResult

interface RegisterRepository {
    suspend fun createUserWithEmail(
        email: String,
        password: String,
    ): AuthResult

    suspend fun saveUserToFirestore(
        email: String,
        name: String,
        level: String,
        interests: List<String>,
    )
}
