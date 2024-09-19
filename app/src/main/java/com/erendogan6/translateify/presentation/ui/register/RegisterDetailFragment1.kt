package com.erendogan6.translateify.presentation.ui.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentRegisterDetail1Binding
import com.erendogan6.translateify.presentation.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDetailFragment1 : Fragment(R.layout.fragment_register_detail1) {
    private var _binding: FragmentRegisterDetail1Binding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: RegisterViewModel by activityViewModels()

    companion object {
        private const val LEVEL_BEGINNER = "beginner"
        private const val LEVEL_INTERMEDIATE = "intermediate"
        private const val LEVEL_ADVANCED = "advanced"
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentRegisterDetail1Binding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            cardOption1.setOnClickListener {
                viewModel.setUserLevel(LEVEL_BEGINNER)
                navigateToNextFragment()
            }
            cardOption2.setOnClickListener {
                viewModel.setUserLevel(LEVEL_INTERMEDIATE)
                navigateToNextFragment()
            }
            cardOption3.setOnClickListener {
                viewModel.setUserLevel(LEVEL_ADVANCED)
                navigateToNextFragment()
            }
        }
    }

    private fun navigateToNextFragment() {
        val action =
            RegisterDetailFragment1Directions
                .actionRegisterDetailFragment1ToRegisterDetailFragment2()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
