package com.erendogan6.translateify.presentation.ui.login

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.erendogan6.translateify.databinding.ActivityLoginBinding
import com.erendogan6.translateify.presentation.MainActivity
import com.erendogan6.translateify.presentation.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        // Back button navigation
        binding.backArrow.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Sign in button
        binding.signInButton.setOnClickListener {
            val email =
                binding.emailInput.text
                    .toString()
                    .trim()
            val password =
                binding.passwordInput.text
                    .toString()
                    .trim()

            viewModel.signInUser(email, password)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.signInButton.isEnabled = !isLoading
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                redirectToMainActivity()
            }
        }
    }

    private fun redirectToMainActivity() {
        startActivity(MainActivity.getIntent(this))
        finish()
    }
}
