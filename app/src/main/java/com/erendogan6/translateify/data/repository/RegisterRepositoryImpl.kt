package com.erendogan6.translateify.data.repository

import com.erendogan6.translateify.domain.repository.RegisterRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RegisterRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : RegisterRepository {
    override suspend fun createUserWithEmail(
        email: String,
        password: String,
    ): AuthResult =
        suspendCoroutine { continuation ->
            firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(task.result!!)
                    } else {
                        continuation.resumeWithException(task.exception ?: Exception("Failed to create register"))
                    }
                }
        }

    override suspend fun saveUserToFirestore(
        email: String,
        name: String,
        level: String,
        interests: List<String>,
    ) {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid ?: throw Exception("User is not authenticated.")

            val registrationDate = System.currentTimeMillis()

            val userPreferences =
                hashMapOf(
                    "email" to email,
                    "name" to name,
                    "registrationDate" to registrationDate,
                    "interests" to interests,
                    "level" to level,
                )

            firestore
                .collection("users")
                .document(userId)
                .set(userPreferences)
                .await()
        } catch (e: Exception) {
            Timber.e("Error saving register to Firestore: " + e.message)
            throw e
        }
    }
}
