package com.erendogan6.translateify.presentation.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentRegisterDetail7Binding
import com.erendogan6.translateify.presentation.MainActivity
import com.erendogan6.translateify.presentation.state.RegistrationResultState
import com.erendogan6.translateify.presentation.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDetailFragment7 : Fragment(R.layout.fragment_register_detail7) {
    private var _binding: FragmentRegisterDetail7Binding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentRegisterDetail7Binding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        viewModel.registerUser()
    }

    private fun observeViewModel() {
        viewModel.registrationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegistrationResultState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is RegistrationResultState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                }
                is RegistrationResultState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast
                        .makeText(
                            requireContext(),
                            "${getString(R.string.registration_failed)}: ${state.message}",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
                else -> Unit
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent =
            Intent(requireContext(), MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
