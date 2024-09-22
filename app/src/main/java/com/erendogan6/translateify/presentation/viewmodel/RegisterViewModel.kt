package com.erendogan6.translateify.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.translateify.R
import com.erendogan6.translateify.domain.usecase.register.RegisterUserUseCase
import com.erendogan6.translateify.domain.usecase.register.SaveUserToFirebaseUseCase
import com.erendogan6.translateify.presentation.state.RegistrationResultState
import com.erendogan6.translateify.utils.ResourcesProvider
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
        private val resourcesProvider: ResourcesProvider,
    ) : ViewModel() {
        private val _registrationState = MutableLiveData<RegistrationResultState>(RegistrationResultState.Idle)
        val registrationState: LiveData<RegistrationResultState> get() = _registrationState

        private val userEmail = MutableLiveData<String>()
        private val userPassword = MutableLiveData<String>()
        private val userName = MutableLiveData<String>()
        private val userLevel = MutableLiveData<String>()
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
                email.isEmpty() -> resourcesProvider.getString(R.string.email_is_required)
                password.isEmpty() -> resourcesProvider.getString(R.string.password_is_required)
                interests.isEmpty() -> resourcesProvider.getString(R.string.at_least_one_interest_must_be_selected)
                else -> null
            }

        fun registerUser() {
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
                    registerUserUseCase(email, password)
                    saveUserToFirebase(name, level, email, interests)
                } catch (e: Exception) {
                    Timber.e(e, resourcesProvider.getString(R.string.registration_failed))
                    _registrationState.value =
                        RegistrationResultState.Error(e.message ?: resourcesProvider.getString(R.string.unknown_error))
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
                saveUserToFirebaseUseCase(email, name, level, interests)
                _registrationState.value = RegistrationResultState.Success
            } catch (e: Exception) {
                Timber.e(
                    e,
                    resourcesProvider.getString(R.string.failed_to_save_user_data_to_firebase),
                )
                _registrationState.value = RegistrationResultState.Error(resourcesProvider.getString(R.string.unknown_error))
            }
        }
    }
