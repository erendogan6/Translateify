package com.erendogan6.translateify.data.repository

import com.erendogan6.translateify.domain.repository.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class LoginRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
) : LoginRepository {
    override suspend fun signInUser(
        email: String,
        password: String,
    ): Result<Unit> =
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
