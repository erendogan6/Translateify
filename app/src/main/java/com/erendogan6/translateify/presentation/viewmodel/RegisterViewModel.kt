package com.erendogan6.translateify.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.domain.usecase.SaveUserToFirebaseUseCase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class RegisterViewModel
    @Inject
    constructor(
        private val saveUserToFirebaseUseCase: SaveUserToFirebaseUseCase,
    ) : ViewModel() {
        // LiveData to manage user details
        private val _userLevel = MutableLiveData<String>()
        val userLevel: LiveData<String> get() = _userLevel

        private val _selectedCategories = MutableLiveData<List<String>>()
        val selectedCategories: LiveData<List<String>> get() = _selectedCategories

        private val _userName = MutableLiveData<String>()
        val userName: LiveData<String> get() = _userName

        private val _userEmail = MutableLiveData<String>()
        val userEmail: LiveData<String> get() = _userEmail

        private val _userPassword = MutableLiveData<String>()
        val userPassword: LiveData<String> get() = _userPassword

        // LiveData for data fetching state
        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean> get() = _isLoading

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?> get() = _errorMessage

        private val _fetchedData = MutableLiveData<List<Word>>()
        val fetchedData: LiveData<List<Word>> get() = _fetchedData

        fun setLoading(isLoading: Boolean) {
            _isLoading.value = isLoading
        }

        // User data setup methods
        fun setUserLevel(level: String) {
            _userLevel.value = level
        }

        fun setSelectedCategories(categories: List<String>) {
            _selectedCategories.value = categories
        }

        fun setUserName(name: String) {
            _userName.value = name
        }

        fun setUserEmail(email: String) {
            _userEmail.value = email
        }

        fun setUserPassword(password: String) {
            _userPassword.value = password
        }

        fun saveUserToFirebase(
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit,
        ) {
            viewModelScope.launch {
                try {
                    // Create the user with FirebaseAuth and suspend until the operation is complete
                    val authResult =
                        createUserWithEmailAndPassword(
                            _userEmail.value.orEmpty(),
                            _userPassword.value.orEmpty(),
                        )

                    // If user creation is successful, get the Firebase User
                    val user = FirebaseAuth.getInstance().currentUser

                    if (user != null) {
                        // Call use case to save user data in Firestore
                        saveUserToFirebaseUseCase(
                            email = _userEmail.value.orEmpty(),
                            name = _userName.value.orEmpty(),
                            level = _userLevel.value.orEmpty(),
                            interests = _selectedCategories.value.orEmpty(),
                            onSuccess = onSuccess,
                            onFailure = onFailure,
                        )
                    } else {
                        onFailure(Exception("User authentication failed."))
                    }
                } catch (e: Exception) {
                    // Handle exceptions from FirebaseAuth or Firestore
                    onFailure(e)
                }
            }
        }

        // A suspending function to handle user creation with Firebase Authentication
        private suspend fun createUserWithEmailAndPassword(
            email: String,
            password: String,
        ): AuthResult =
            suspendCoroutine { continuation ->
                FirebaseAuth
                    .getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(task.result!!)
                        } else {
                            continuation.resumeWithException(task.exception ?: Exception("Unknown error occurred"))
                        }
                    }
            }
    }
