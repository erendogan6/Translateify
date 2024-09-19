package com.erendogan6.translateify.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.translateify.domain.usecase.register.RegisterUserUseCase
import com.erendogan6.translateify.domain.usecase.register.SaveUserToFirebaseUseCase
import com.erendogan6.translateify.presentation.ui.state.RegistrationResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
    @Inject
    constructor(
        private val registerUserUseCase: RegisterUserUseCase,
        private val saveUserToFirebaseUseCase: SaveUserToFirebaseUseCase,
        private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    ) : ViewModel() {
        private val _registrationState = MutableLiveData<RegistrationResultState>(RegistrationResultState.Idle)
        val registrationState: LiveData<RegistrationResultState> get() = _registrationState

        val userEmail = MutableLiveData<String>()
        val userPassword = MutableLiveData<String>()
        val userName = MutableLiveData<String>()
        val userLevel = MutableLiveData<String>()
        val selectedCategories = MutableLiveData<List<String>>()

        fun setUserEmail(email: String) {
            userEmail.value = email
        }

        fun setUserPassword(password: String) {
            userPassword.value = password
        }

        fun setUserName(name: String) {
            userName.value = name
        }

        fun setUserLevel(level: String) {
            userLevel.value = level
        }

        fun setSelectedCategories(categories: List<String>) {
            selectedCategories.value = categories
        }

        private fun validateInputs(
            email: String,
            password: String,
            interests: List<String>,
        ): String? =
            when {
                email.isEmpty() -> "Email is required"
                password.isEmpty() -> "Password is required"
                interests.isEmpty() -> "At least one interest must be selected"
                else -> null
            }

        fun registerUser() {
            Timber.d("registerUser called")

            val email = userEmail.value.orEmpty()
            val password = userPassword.value.orEmpty()
            val name = userName.value.orEmpty()
            val level = userLevel.value.orEmpty()
            val interests = selectedCategories.value.orEmpty()

            val errorMessage = validateInputs(email, password, interests)
            if (errorMessage != null) {
                _registrationState.value = RegistrationResultState.Error(errorMessage)
                return
            }

            viewModelScope.launch(dispatcher) {
                _registrationState.value = RegistrationResultState.Loading
                try {
                    Timber.d("Attempting to register user with email: $email")
                    registerUserUseCase(email, password)
                    Timber.d("User registration successful")

                    saveUserToFirebase(name, level, email, interests)
                } catch (e: Exception) {
                    Timber.e(e, "Registration failed")
                    _registrationState.value = RegistrationResultState.Error(e.message ?: "Unknown error")
                }
            }
        }

        private suspend fun saveUserToFirebase(
            name: String,
            level: String,
            email: String,
            interests: List<String>,
        ) {
            try {
                Timber.d("Saving user data to Firebase")
                saveUserToFirebaseUseCase(email, name, level, interests)
                Timber.d("User data saved successfully")
                _registrationState.value = RegistrationResultState.Success
            } catch (e: Exception) {
                Timber.e(e, "Failed to save user data to Firebase")
                _registrationState.value = RegistrationResultState.Error(e.message ?: "Unknown error")
            }
        }
    }
