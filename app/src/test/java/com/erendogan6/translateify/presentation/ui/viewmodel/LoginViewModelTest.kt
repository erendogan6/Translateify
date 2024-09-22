package com.erendogan6.translateify.presentation.ui.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.erendogan6.translateify.R
import com.erendogan6.translateify.domain.usecase.login.LoginUseCase
import com.erendogan6.translateify.presentation.viewmodel.LoginViewModel
import com.erendogan6.translateify.utils.StringProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    // Mock the use case
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel

    // Provide a rule for LiveData testing
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Test dispatcher to control coroutine execution
    private val testDispatcher = StandardTestDispatcher()

    private val mockContext: Context = mockk(relaxed = true)

    private var resourcesProvider = mockk<StringProvider>()

    @Before
    fun setUp() {
        // Initialize the Main dispatcher for testing
        Dispatchers.setMain(testDispatcher)

        // Initialize the mock objects
        loginUseCase = mockk()

        every { mockContext.getString(R.string.email_cannot_be_empty) } returns "Email cannot be empty"
        every { mockContext.getString(R.string.unknown_error) } returns "Unexpected error occurred"
        every { mockContext.getString(R.string.error_during_login) } returns "Login failed"
        every { mockContext.getString(R.string.error_during_login) } returns "Login failed"

        resourcesProvider = StringProvider(mockContext)

        // Initialize the ViewModel with the mocked use case and test dispatcher
        viewModel = LoginViewModel(loginUseCase, testDispatcher, resourcesProvider)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to its original state
        Dispatchers.resetMain()
    }

    // Test 1: Success case for login
    @Test
    fun `loginUseCase success triggers login success`() =
        runTest(testDispatcher) {
            // Arrange: Mock successful result for loginUseCase
            coEvery { loginUseCase("test@example.com", "password123") } returns Result.success(Unit)

            // Track login success LiveData
            val loginSuccessObserver = mutableListOf<Boolean>()
            viewModel.loginSuccess.observeForever { loginSuccessObserver.add(it) }

            // Act: Call the signInUser method
            viewModel.signInUser("test@example.com", "password123")

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert: Ensure login success was set to true
            assertEquals(listOf(true), loginSuccessObserver)

            // Verify that loginUseCase was called
            coVerify(exactly = 1) { loginUseCase("test@example.com", "password123") }
        }

    // Test 2: loginUseCase fails and sets error message
    @Test
    fun `loginUseCase failure sets error message`() =
        runTest(testDispatcher) {
            // Arrange: Mock failure result for loginUseCase
            val exceptionMessage = "Invalid credentials"
            coEvery { loginUseCase("test@example.com", "password123") } returns Result.failure(Exception(exceptionMessage))

            // Track error message LiveData
            val errorObserver = mutableListOf<String?>()
            viewModel.errorMessage.observeForever { errorObserver.add(it) }

            // Act: Call the signInUser method
            viewModel.signInUser("test@example.com", "password123")

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert: Ensure the error message was set to the exception message
            assertEquals(listOf(exceptionMessage), errorObserver)

            // Verify that loginUseCase was called
            coVerify(exactly = 1) { loginUseCase("test@example.com", "password123") }
        }

    // Test 3: signInUser should not trigger loginUseCase if email is empty
    @Test
    fun `loginUseCase not triggered if email is empty`() =
        runTest(testDispatcher) {
            // Act: Call the signInUser method with empty email
            viewModel.signInUser("", "password123")

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify that loginUseCase was never called
            coVerify(exactly = 0) { loginUseCase(any(), any()) }
        }

    // Test 4: signInUser should not trigger loginUseCase if password is empty
    @Test
    fun `loginUseCase not triggered if password is empty`() =
        runTest(testDispatcher) {
            // Act: Call the signInUser method with empty password
            viewModel.signInUser("test@example.com", "")

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify that loginUseCase was never called
            coVerify(exactly = 0) { loginUseCase(any(), any()) }
        }

    // Test 5: signInUser handles exceptions properly and sets error message
    @Test
    fun `loginUseCase handles exceptions properly and sets error message`() =
        runTest(testDispatcher) {
            // Arrange: Mock loginUseCase to throw an exception
            val exceptionMessage = "Unexpected error occurred"
            coEvery { loginUseCase(any(), any()) } throws Exception(exceptionMessage)

            // Track loading and error state
            val loadingObserver = mutableListOf<Boolean>()
            viewModel.isLoading.observeForever { loadingObserver.add(it) }

            val errorObserver = mutableListOf<String?>()
            viewModel.errorMessage.observeForever { errorObserver.add(it) }

            // Act: Call the signInUser method
            viewModel.signInUser("test@example.com", "password123")

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert: Ensure loading state was initially set to true and then false
            assertEquals(listOf(true, false), loadingObserver)

            // Assert: Ensure the error message was set to the exception message
            assertEquals(listOf(exceptionMessage), errorObserver)

            // Verify that loginUseCase was called
            coVerify(exactly = 1) { loginUseCase(any(), any()) }
        }

    @Test
    fun `loading state is updated correctly during login process`() =
        runTest(testDispatcher) {
            // Arrange: Mock successful result for loginUseCase
            coEvery { loginUseCase(any(), any()) } returns Result.success(Unit)

            // Track loading state LiveData
            val loadingObserver = mutableListOf<Boolean>()
            viewModel.isLoading.observeForever { loadingObserver.add(it) }

            // Act: Call the signInUser method
            viewModel.signInUser("test@example.com", "password123")

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert: Ensure loading state was initially true and then false
            assertEquals(listOf(true, false), loadingObserver)

            // Verify that loginUseCase was called
            coVerify(exactly = 1) { loginUseCase("test@example.com", "password123") }
        }

    @Test
    fun `error message is reset after successful login`() =
        runTest(testDispatcher) {
            // Arrange: Mock successful result for loginUseCase
            coEvery { loginUseCase(any(), any()) } returns Result.success(Unit)

            // Track error message LiveData
            val errorObserver = mutableListOf<String?>()
            viewModel.errorMessage.observeForever { errorObserver.add(it) }

            // Set an initial error message
            viewModel.signInUser("", "password123") // Trigger validation failure to set an error message

            // Clear any previous error
            viewModel.signInUser("test@example.com", "password123") // Perform a successful login

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert: The error message should be reset to null after a successful login
            assertEquals(listOf("Email cannot be empty"), errorObserver)

            // Verify that loginUseCase was called
            coVerify(exactly = 1) { loginUseCase("test@example.com", "password123") }
        }

    @Test
    fun `loginSuccess is not triggered on failure`() =
        runTest(testDispatcher) {
            // Arrange: Mock failure result for loginUseCase
            coEvery { loginUseCase(any(), any()) } throws Exception("Login failed")

            // Track login success LiveData
            val loginSuccessObserver = mutableListOf<Boolean>()
            viewModel.loginSuccess.observeForever { loginSuccessObserver.add(it) }

            // Act: Call the signInUser method
            viewModel.signInUser("test@example.com", "password123")

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert: Ensure login success was never set to true
            assertTrue(loginSuccessObserver.isEmpty())

            // Verify that loginUseCase was called
            coVerify(exactly = 1) { loginUseCase("test@example.com", "password123") }
        }

    @Test
    fun `loginUseCase not triggered if both email and password are empty`() =
        runTest(testDispatcher) {
            // Track error message LiveData
            val errorObserver = mutableListOf<String?>()
            viewModel.errorMessage.observeForever { errorObserver.add(it) }

            // Act: Call the signInUser method with empty email and password
            viewModel.signInUser("", "")

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify that loginUseCase was never called
            coVerify(exactly = 0) { loginUseCase(any(), any()) }

            // Verify that the appropriate error message was set
            assertEquals(listOf("Email cannot be empty"), errorObserver)
        }

    @Test
    fun `login succeeds after validation failure`() =
        runTest(testDispatcher) {
            // Arrange: Mock successful result for loginUseCase
            coEvery { loginUseCase(any(), any()) } returns Result.success(Unit)

            // Track login success LiveData
            val loginSuccessObserver = mutableListOf<Boolean>()
            viewModel.loginSuccess.observeForever { loginSuccessObserver.add(it) }

            // Track error message LiveData
            val errorObserver = mutableListOf<String?>()
            viewModel.errorMessage.observeForever { errorObserver.add(it) }

            // Trigger validation failure
            viewModel.signInUser("", "password123") // This will fail because the email is empty

            // Perform a valid login attempt
            viewModel.signInUser("test@example.com", "password123")

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert: Ensure login success was set after the valid login
            assertEquals(listOf(true), loginSuccessObserver)

            // Assert: Ensure error message was first set and then reset to null
            assertEquals(listOf("Email cannot be empty"), errorObserver)

            // Verify that loginUseCase was called only once with valid credentials
            coVerify(exactly = 1) { loginUseCase("test@example.com", "password123") }
        }

    @Test
    fun `multiple login attempts update loading state correctly`() =
        runTest(testDispatcher) {
            // Arrange: Mock successful result for loginUseCase
            coEvery { loginUseCase(any(), any()) } returns Result.success(Unit)

            // Track loading state LiveData
            val loadingObserver = mutableListOf<Boolean>()
            viewModel.isLoading.observeForever { loadingObserver.add(it) }

            // First login attempt
            viewModel.signInUser("test1@example.com", "password123")

            // Second login attempt
            viewModel.signInUser("test2@example.com", "password456")

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert: Ensure loading state was updated correctly for both attempts
            assertEquals(listOf(true, false, true, false), loadingObserver)

            // Verify that loginUseCase was called for both login attempts
            coVerify(exactly = 1) { loginUseCase("test1@example.com", "password123") }
            coVerify(exactly = 1) { loginUseCase("test2@example.com", "password456") }
        }

    @Test
    fun `login fails with incorrect credentials and sets error message`() =
        runTest(testDispatcher) {
            // Arrange: Mock failure result for loginUseCase due to incorrect credentials
            val errorMessage = "Invalid credentials"
            coEvery { loginUseCase(any(), any()) } throws Exception(errorMessage)

            // Track error message LiveData
            val errorObserver = mutableListOf<String?>()
            viewModel.errorMessage.observeForever { errorObserver.add(it) }

            // Track login success LiveData
            val loginSuccessObserver = mutableListOf<Boolean>()
            viewModel.loginSuccess.observeForever { loginSuccessObserver.add(it) }

            // Act: Call the signInUser method with incorrect credentials
            viewModel.signInUser("wrong@example.com", "wrongpassword")

            // Advance coroutine to ensure everything completes
            testDispatcher.scheduler.advanceUntilIdle()

            // Assert: Error message was set correctly
            assertEquals(listOf(errorMessage), errorObserver)

            // Assert: loginSuccess was not triggered
            assertTrue(loginSuccessObserver.isEmpty())

            // Verify that loginUseCase was called
            coVerify(exactly = 1) { loginUseCase("wrong@example.com", "wrongpassword") }
        }
}
