package com.erendogan6.translateify.presentation.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentRegisterDetail5Binding
import com.erendogan6.translateify.presentation.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDetailFragment5 : Fragment(R.layout.fragment_register_detail5) {
    private var _binding: FragmentRegisterDetail5Binding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentRegisterDetail5Binding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.continueButton.setOnClickListener {
            val password = binding.passwordInput.text.toString()
            if (password.length >= 6) {
                viewModel.setUserPassword(password)
                navigateToNextFragment()
            } else {
                binding.passwordInputLayout.error = getString(R.string.error_password)
            }
        }

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun navigateToNextFragment() {
        val action =
            RegisterDetailFragment5Directions
                .actionRegisterDetailFragment5ToRegisterDetailFragment6()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
