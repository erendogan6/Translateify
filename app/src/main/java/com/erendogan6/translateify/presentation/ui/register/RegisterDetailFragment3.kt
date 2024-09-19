package com.erendogan6.translateify.presentation.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentRegisterDetail3Binding
import com.erendogan6.translateify.presentation.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDetailFragment3 : Fragment(R.layout.fragment_register_detail3) {
    private var _binding: FragmentRegisterDetail3Binding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentRegisterDetail3Binding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.continueButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            if (name.isNotEmpty()) {
                viewModel.setUserName(name)
                navigateToNextFragment()
            } else {
                binding.nameInputLayout.error = getString(R.string.error_name)
            }
        }

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun navigateToNextFragment() {
        val action =
            RegisterDetailFragment3Directions
                .actionRegisterDetailFragment3ToRegisterDetailFragment4()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
