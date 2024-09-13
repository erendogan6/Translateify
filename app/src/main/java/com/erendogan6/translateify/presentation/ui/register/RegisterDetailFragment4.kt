package com.erendogan6.translateify.presentation.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.erendogan6.translateify.databinding.FragmentRegisterDetail4Binding
import com.erendogan6.translateify.presentation.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDetailFragment4 : Fragment() {
    private var _binding: FragmentRegisterDetail4Binding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegisterDetail4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
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
                binding.emailInputLayout.error = "Geçerli bir e-posta adresi girin."
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
