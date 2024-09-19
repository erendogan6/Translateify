package com.erendogan6.translateify.presentation.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentRegisterDetail2Binding
import com.erendogan6.translateify.domain.model.Category
import com.erendogan6.translateify.presentation.adapter.CategoryAdapter
import com.erendogan6.translateify.presentation.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDetailFragment2 : Fragment(R.layout.fragment_register_detail2) {
    private var _binding: FragmentRegisterDetail2Binding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by activityViewModels()
    private val maxSelection = 3

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentRegisterDetail2Binding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        val categories =
            listOf(
                Category(getString(R.string.daily_life_and_communication), R.drawable.ic_category_daily),
                Category(getString(R.string.emotions_and_expressions), R.drawable.ic_category_emotions),
                Category(getString(R.string.food_and_drinks), R.drawable.ic_category_food),
                Category(getString(R.string.health_and_body), R.drawable.ic_category_health),
                Category(getString(R.string.travelers_and_tourists), R.drawable.ic_category_travel),
                Category(getString(R.string.technology), R.drawable.ic_category_tech),
                Category(getString(R.string.science), R.drawable.ic_category_science),
                Category(getString(R.string.nature_and_environment), R.drawable.ic_category_nature),
            )

        val adapter =
            CategoryAdapter(categories, maxSelection) { selected ->
                viewModel.setSelectedCategories(selected)
            }

        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCategories.adapter = adapter

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.continueButton.setOnClickListener {
            validateAndNavigate()
        }
    }

    private fun validateAndNavigate() {
        val selectedCount = viewModel.selectedCategories.value?.size ?: 0
        if (selectedCount in 1..maxSelection) {
            navigateToNextFragment()
        } else {
            Toast.makeText(requireContext(), getString(R.string.select_at_least_one_category), Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToNextFragment() {
        val action = RegisterDetailFragment2Directions.actionRegisterDetailFragment2ToRegisterDetailFragment3()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
