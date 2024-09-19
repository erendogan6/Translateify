package com.erendogan6.translateify.presentation.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentRegisterDetail4Binding
import com.erendogan6.translateify.presentation.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDetailFragment4 : Fragment(R.layout.fragment_register_detail4) {
    private var _binding: FragmentRegisterDetail4Binding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentRegisterDetail4Binding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.continueButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            if (email.isNotEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()
            ) {
                viewModel.setUserEmail(email)
                navigateToNextFragment()
            } else {
                binding.emailInputLayout.error = getString(R.string.please_input_mail)
            }
        }

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun navigateToNextFragment() {
        val action =
            RegisterDetailFragment4Directions
                .actionRegisterDetailFragment4ToRegisterDetailFragment5()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
