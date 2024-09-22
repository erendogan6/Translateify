package com.erendogan6.translateify.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.translateify.R
import com.erendogan6.translateify.domain.usecase.login.LoginUseCase
import com.erendogan6.translateify.utils.ResourcesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
        private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
        private val resourcesProvider: ResourcesProvider,
    ) : ViewModel() {
        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean> get() = _isLoading

        private val _errorMessage = MutableLiveData<String?>()
        val errorMessage: LiveData<String?> get() = _errorMessage

        private val _loginSuccess = MutableLiveData<Boolean>()
        val loginSuccess: LiveData<Boolean> get() = _loginSuccess

        fun signInUser(
            email: String,
            password: String,
        ) {
            if (email.isBlank()) {
                _errorMessage.value =
                    resourcesProvider.getString(R.string.email_cannot_be_empty)
                return
            }

            if (password.isBlank()) {
                _errorMessage.value =
                    resourcesProvider.getString(R.string.password_cannot_be_empty)
                return
            }

            viewModelScope.launch(dispatcher) {
                try {
                    _isLoading.value = true

                    val result = loginUseCase(email, password)

                    result.fold(
                        onSuccess = {
                            _isLoading.value = false
                            _loginSuccess.value = true
                        },
                        onFailure = { exception ->
                            _isLoading.value = false
                            _errorMessage.value = exception.message
                        },
                    )
                } catch (e: Exception) {
                    _isLoading.value = false
                    _errorMessage.value = e.message
                    Timber.e(
                        e.message?.let {
                            resourcesProvider.getString(
                                R.string.error_during_login,
                                it,
                            )
                        },
                    )
                }
            }
        }
    }
