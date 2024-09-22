package com.erendogan6.translateify.presentation.ui.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.erendogan6.translateify.R
import com.erendogan6.translateify.domain.usecase.register.RegisterUserUseCase
import com.erendogan6.translateify.domain.usecase.register.SaveUserToFirebaseUseCase
import com.erendogan6.translateify.presentation.state.RegistrationResultState
import com.erendogan6.translateify.presentation.viewmodel.RegisterViewModel
import com.erendogan6.translateify.utils.StringProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {
    // LiveData testing rule
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Mocking the use cases
    private lateinit var registerUserUseCase: RegisterUserUseCase
    private lateinit var saveUserToFirebaseUseCase: SaveUserToFirebaseUseCase
    private lateinit var viewModel: RegisterViewModel

    // Test dispatcher for controlling coroutine execution
    private val testDispatcher = StandardTestDispatcher()

    private val context: Context = mockk(relaxed = true)

    private var resourcesProvider = mockk<StringProvider>()

    @Before
    fun setUp() {
        // Initialize the Main dispatcher for testing
        Dispatchers.setMain(testDispatcher)

        // Initialize the mock objects
        registerUserUseCase = mockk()
        saveUserToFirebaseUseCase = mockk()

        every { context.getString(R.string.unknown_error) } returns "Unknown error"
        every { context.getString(R.string.registration_failed) } returns "Registration failed"
        every { context.getString(R.string.email_is_required) } returns "Email is required"
        every { context.getString(R.string.password_is_required) } returns "Password is required"
        every { context.getString(R.string.at_least_one_interest_must_be_selected) } returns "At least one interest must be selected"
        every { context.getString(R.string.failed_to_save_user_data_to_firebase) } returns "Failed to save to Firestore"
        resourcesProvider = StringProvider(context)

        // Initialize the ViewModel with mocked use cases and the test dispatcher
        viewModel = RegisterViewModel(registerUserUseCase, saveUserToFirebaseUseCase, testDispatcher, resourcesProvider)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to the original state after tests
        Dispatchers.resetMain()
    }

    // Test 1: Success case for both registerUserUseCase and saveUserToFirebaseUseCase
    @Test
    fun registerUser_success_triggers_SaveUserToFirebaseUseCase() =
        runTest(testDispatcher) {
            // Arrange: Mock successful result for registerUserUseCase
            coEvery { registerUserUseCase(any(), any()) } returns Unit

            // Arrange: Mock successful result for saveUserToFirebaseUseCase
            coEvery { saveUserToFirebaseUseCase(any(), any(), any(), any()) } returns Unit

            // Set the required data in the ViewModel before calling the method
            viewModel.setUserEmail("test@example.com")
            viewModel.setUserPassword("password123")
            viewModel.setUserName("John Doe")
            viewModel.setUserLevel("Beginner")
            viewModel.setSelectedCategories(listOf("Language", "Music"))

            // Observe registrationState LiveData
            val stateObserver = mutableListOf<RegistrationResultState>()
            viewModel.registrationState.observeForever { stateObserver.add(it) }

            // Act: Call the ViewModel method after setting the required fields
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify that registerUserUseCase was called with the correct parameters
            coVerify(exactly = 1) {
                registerUserUseCase("test@example.com", "password123")
            }

            // Verify that saveUserToFirebaseUseCase was called with the correct parameters
            coVerify(exactly = 1) {
                saveUserToFirebaseUseCase(
                    "test@example.com",
                    "John Doe",
                    "Beginner",
                    listOf("Language", "Music"),
                )
            }

            // Assert that the registrationState was updated correctly
            assertThat(stateObserver).containsExactly(
                RegistrationResultState.Idle,
                RegistrationResultState.Loading,
                RegistrationResultState.Success,
            )
        }

    // Test 2: registerUserUseCase fails, sets error message, and saveUserToFirebaseUseCase is not called
    @Test
    fun registerUser_failure_sets_error_message_and_does_not_call_SaveUserToFirebaseUseCase() =
        runTest(testDispatcher) {
            // Arrange: Mock failure result for registerUserUseCase
            coEvery { registerUserUseCase(any(), any()) } throws Exception("Registration failed")

            // Observe registrationState LiveData
            val stateObserver = mutableListOf<RegistrationResultState>()
            viewModel.registrationState.observeForever { stateObserver.add(it) }

            // Set the ViewModel data before calling the method
            viewModel.setUserEmail("test@example.com")
            viewModel.setUserPassword("password123")
            viewModel.setSelectedCategories(listOf("Technology")) // To pass validation

            // Act: Call the registerUser method
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify that registerUserUseCase was called and failed
            coVerify(exactly = 1) {
                registerUserUseCase("test@example.com", "password123")
            }

            // Verify that SaveUserToFirebaseUseCase was not called
            coVerify(exactly = 0) {
                saveUserToFirebaseUseCase(any(), any(), any(), any())
            }

            // Assert that the registrationState was updated correctly
            assertThat(stateObserver).containsExactly(
                RegistrationResultState.Idle,
                RegistrationResultState.Loading,
                RegistrationResultState.Error("Registration failed"),
            )
        }

    // Test 3: saveUserToFirebaseUseCase failure sets error message
    @Test
    fun saveUserToFirebaseUseCase_failure_sets_error_message() =
        runTest(testDispatcher) {
            // Arrange: Mock successful result for registerUserUseCase
            coEvery { registerUserUseCase(any(), any()) } returns Unit

            // Arrange: Mock failure result for saveUserToFirebaseUseCase
            coEvery { saveUserToFirebaseUseCase(any(), any(), any(), any()) } throws Exception("Saving to Firestore failed")

            // Observe registrationState LiveData
            val stateObserver = mutableListOf<RegistrationResultState>()
            viewModel.registrationState.observeForever { stateObserver.add(it) }

            // Set the ViewModel data before calling the method
            viewModel.setUserEmail("test@example.com")
            viewModel.setUserPassword("password123")
            viewModel.setUserName("John Doe")
            viewModel.setUserLevel("Beginner")
            viewModel.setSelectedCategories(listOf("Language", "Music"))

            // Act: Call the registerUser method
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify the expected behavior
            coVerify(exactly = 1) {
                registerUserUseCase("test@example.com", "password123")
            }
            coVerify(exactly = 1) {
                saveUserToFirebaseUseCase(
                    "test@example.com",
                    "John Doe",
                    "Beginner",
                    listOf("Language", "Music"),
                )
            }

            // Assert that the registrationState was updated correctly
            assertThat(stateObserver).containsExactly(
                RegistrationResultState.Idle,
                RegistrationResultState.Loading,
                RegistrationResultState.Error("Unknown error"),
            )
        }

    @Test
    fun registerUser_does_not_trigger_RegisterUserUseCase_if_email_is_empty() =
        runTest(testDispatcher) {
            // Set only the password
            viewModel.setUserEmail("")
            viewModel.setUserPassword("password123")

            // Observe registrationState LiveData
            val stateObserver = mutableListOf<RegistrationResultState>()
            viewModel.registrationState.observeForever { stateObserver.add(it) }

            // Act: Call the registerUser method
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify that registerUserUseCase was never called
            coVerify(exactly = 0) {
                registerUserUseCase(any(), any())
            }

            // Assert that the registrationState was updated correctly
            assertThat(stateObserver).containsExactly(
                RegistrationResultState.Idle,
                RegistrationResultState.Error("Email is required"),
            )
        }

    @Test
    fun registerUser_does_not_trigger_RegisterUserUseCase_if_password_is_empty() =
        runTest(testDispatcher) {
            // Set only the email
            viewModel.setUserEmail("test@example.com")
            viewModel.setUserPassword("")

            // Observe registrationState LiveData
            val stateObserver = mutableListOf<RegistrationResultState>()
            viewModel.registrationState.observeForever { stateObserver.add(it) }

            // Act: Call the registerUser method
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify that registerUserUseCase was never called
            coVerify(exactly = 0) {
                registerUserUseCase(any(), any())
            }

            // Assert that the registrationState was updated correctly
            assertThat(stateObserver).containsExactly(
                RegistrationResultState.Idle,
                RegistrationResultState.Error("Password is required"),
            )
        }

    @Test
    fun saveUserToFirebaseUseCase_not_triggered_if_no_interests_are_selected() =
        runTest(testDispatcher) {
            // Set the ViewModel data with no interests selected
            viewModel.setUserEmail("test@example.com")
            viewModel.setUserPassword("password123")
            viewModel.setUserName("John Doe")
            viewModel.setUserLevel("Beginner")
            viewModel.setSelectedCategories(emptyList()) // No interests

            // Observe registrationState LiveData
            val stateObserver = mutableListOf<RegistrationResultState>()
            viewModel.registrationState.observeForever { stateObserver.add(it) }

            // Act: Call the registerUser method
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify that registerUserUseCase was not called
            coVerify(exactly = 0) {
                registerUserUseCase(any(), any())
            }

            // Verify that saveUserToFirebaseUseCase was never called
            coVerify(exactly = 0) {
                saveUserToFirebaseUseCase(any(), any(), any(), any())
            }

            // Assert that the registrationState was updated correctly
            assertThat(stateObserver).containsExactly(
                RegistrationResultState.Idle,
                RegistrationResultState.Error("At least one interest must be selected"),
            )
        }

    @Test
    fun loading_state_is_updated_correctly_during_registerUser() =
        runTest(testDispatcher) {
            // Arrange: Mock successful result for registerUserUseCase and saveUserToFirebaseUseCase
            coEvery { registerUserUseCase(any(), any()) } returns Unit
            coEvery { saveUserToFirebaseUseCase(any(), any(), any(), any()) } returns Unit

            // Observe registrationState LiveData
            val stateObserver = mutableListOf<RegistrationResultState>()
            viewModel.registrationState.observeForever { stateObserver.add(it) }

            // Set the ViewModel data
            viewModel.setUserEmail("test@example.com")
            viewModel.setUserPassword("password123")
            viewModel.setUserName("John Doe")
            viewModel.setUserLevel("Beginner")
            viewModel.setSelectedCategories(listOf("Language"))

            // Act: Call the registerUser method
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert that registrationState was updated correctly
            assertThat(stateObserver).containsExactly(
                RegistrationResultState.Idle,
                RegistrationResultState.Loading,
                RegistrationResultState.Success,
            )
        }

    @Test
    fun failure_sets_error_message_and_stops_loading() =
        runTest(testDispatcher) {
            // Arrange: Mock failure for registerUserUseCase
            coEvery { registerUserUseCase(any(), any()) } throws Exception("Test failure")

            // Observe registrationState LiveData
            val stateObserver = mutableListOf<RegistrationResultState>()
            viewModel.registrationState.observeForever { stateObserver.add(it) }

            // Set the ViewModel data
            viewModel.setUserEmail("test@example.com")
            viewModel.setUserPassword("password123")
            viewModel.setSelectedCategories(listOf("Technology")) // To pass validation

            // Act: Call the registerUser method
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert: registrationState was updated correctly
            assertThat(stateObserver).containsExactly(
                RegistrationResultState.Idle,
                RegistrationResultState.Loading,
                RegistrationResultState.Error("Test failure"),
            )
        }

    @Test
    fun registerUserUseCase_called_with_correct_arguments() =
        runTest(testDispatcher) {
            // Arrange: Mock success for registerUserUseCase
            coEvery { registerUserUseCase(any(), any()) } returns Unit

            // Set the ViewModel data
            viewModel.setUserEmail("test@example.com")
            viewModel.setUserPassword("password123")
            viewModel.setSelectedCategories(listOf("Technology")) // To pass validation

            // Act: Call the registerUser method
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify the correct arguments were passed to registerUserUseCase
            coVerify(exactly = 1) {
                registerUserUseCase(
                    email = "test@example.com",
                    password = "password123",
                )
            }
        }

    @Test
    fun registerUser_does_not_trigger_RegisterUserUseCase_if_both_email_and_password_are_missing() =
        runTest(testDispatcher) {
            // Set no email and password
            viewModel.setUserEmail("")
            viewModel.setUserPassword("")
            viewModel.setSelectedCategories(listOf("Technology")) // To pass interests validation

            // Observe registrationState LiveData
            val stateObserver = mutableListOf<RegistrationResultState>()
            viewModel.registrationState.observeForever { stateObserver.add(it) }

            // Act: Call the registerUser method
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify that registerUserUseCase was never called
            coVerify(exactly = 0) {
                registerUserUseCase(any(), any())
            }

            // Verify that appropriate error message was set
            assertThat(stateObserver).containsExactly(
                RegistrationResultState.Idle,
                RegistrationResultState.Error("Email is required"),
            )
        }

    @Test
    fun registerUser_fails_and_does_not_call_SaveUserToFirebaseUseCase_if_registration_fails() =
        runTest(testDispatcher) {
            // Arrange: Mock failure result for registerUserUseCase
            coEvery { registerUserUseCase(any(), any()) } throws Exception("Registration failed")

            // Observe registrationState LiveData
            val stateObserver = mutableListOf<RegistrationResultState>()
            viewModel.registrationState.observeForever { stateObserver.add(it) }

            // Set email and password in the ViewModel
            viewModel.setUserEmail("test@example.com")
            viewModel.setUserPassword("password123")
            viewModel.setUserName("John Doe")
            viewModel.setUserLevel("Intermediate")
            viewModel.setSelectedCategories(listOf("Language", "Technology"))

            // Act: Call the registerUser method
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify that registerUserUseCase was called and failed
            coVerify(exactly = 1) {
                registerUserUseCase(any(), any())
            }

            // Verify that saveUserToFirebaseUseCase was never called
            coVerify(exactly = 0) {
                saveUserToFirebaseUseCase(any(), any(), any(), any())
            }

            // Assert: registrationState was set correctly
            assertThat(stateObserver).containsExactly(
                RegistrationResultState.Idle,
                RegistrationResultState.Loading,
                RegistrationResultState.Error("Registration failed"),
            )
        }

    @Test
    fun registerUser_triggers_SaveUserToFirebaseUseCase_even_if_name_is_not_provided() =
        runTest(testDispatcher) {
            // Arrange: Mock successful result for registerUserUseCase
            coEvery { registerUserUseCase(any(), any()) } returns Unit

            // Arrange: Mock successful result for saveUserToFirebaseUseCase
            coEvery { saveUserToFirebaseUseCase(any(), any(), any(), any()) } returns Unit

            // Set ViewModel values with no name provided
            viewModel.setUserEmail("test@example.com")
            viewModel.setUserPassword("password123")
            viewModel.setUserLevel("Beginner")
            viewModel.setSelectedCategories(listOf("Technology"))

            // Act: Call the registerUser method
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify that registerUserUseCase was called
            coVerify(exactly = 1) {
                registerUserUseCase(any(), any())
            }

            // Verify that saveUserToFirebaseUseCase was called with an empty name
            coVerify(exactly = 1) {
                saveUserToFirebaseUseCase(
                    email = "test@example.com",
                    name = "", // Name is empty
                    level = "Beginner",
                    interests = listOf("Technology"),
                )
            }
        }

    @Test
    fun registerUser_handles_exception_and_sets_error_message() =
        runTest(testDispatcher) {
            // Arrange: Mock registerUserUseCase to throw an exception
            val exceptionMessage = "Unexpected error occurred"
            coEvery { registerUserUseCase(any(), any()) } throws Exception(exceptionMessage)

            // Observe registrationState LiveData
            val stateObserver = mutableListOf<RegistrationResultState>()
            viewModel.registrationState.observeForever { stateObserver.add(it) }

            // Set email and password in the ViewModel
            viewModel.setUserEmail("test@example.com")
            viewModel.setUserPassword("password123")
            viewModel.setSelectedCategories(listOf("Technology")) // To pass validation

            // Act: Call the registerUser method
            viewModel.registerUser()

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert: Check that registerUserUseCase was called and threw an exception
            coVerify(exactly = 1) { registerUserUseCase(any(), any()) }

            // Assert: Ensure the registrationState was updated correctly
            assertThat(stateObserver).containsExactly(
                RegistrationResultState.Idle,
                RegistrationResultState.Loading,
                RegistrationResultState.Error(exceptionMessage),
            )
        }
}
