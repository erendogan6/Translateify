package com.erendogan6.translateify.presentation.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.erendogan6.translateify.databinding.FragmentRegisterDetail1Binding
import com.erendogan6.translateify.presentation.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDetailFragment1 : Fragment() {
    private var _binding: FragmentRegisterDetail1Binding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegisterDetail1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardOption1.setOnClickListener {
            viewModel.setUserLevel("beginner")
            navigateToNextFragment()
        }

        binding.cardOption2.setOnClickListener {
            viewModel.setUserLevel("intermediate")
            navigateToNextFragment()
        }

        binding.cardOption3.setOnClickListener {
            viewModel.setUserLevel("advanced")
            navigateToNextFragment()
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
