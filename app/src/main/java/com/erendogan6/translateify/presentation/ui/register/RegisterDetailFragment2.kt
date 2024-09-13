package com.erendogan6.translateify.presentation.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentRegisterDetail2Binding
import com.erendogan6.translateify.presentation.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterDetailFragment2 : Fragment() {
    private var _binding: FragmentRegisterDetail2Binding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by activityViewModels()

    private val selectedCategories = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegisterDetail2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryCardListeners()

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.continueButton.setOnClickListener {
            validateAndNavigate()
        }
    }

    private fun setupCategoryCardListeners() {
        // Set click listeners for category cards
        binding.cardOption1.setOnClickListener { toggleCategory("Günlük Yaşam ve İletişim", binding.cardOption1) }
        binding.cardOption2.setOnClickListener { toggleCategory("Duygular ve İfadeler", binding.cardOption2) }
        binding.cardOption3.setOnClickListener { toggleCategory("Gezginler ve Turistler", binding.cardOption3) }
        binding.cardOption4.setOnClickListener { toggleCategory("Yiyecek ve İçecek", binding.cardOption4) }
        binding.cardOption5.setOnClickListener { toggleCategory("Teknoloji", binding.cardOption5) }
        binding.cardOption6.setOnClickListener { toggleCategory("Doğa ve Çevre", binding.cardOption6) }
        binding.cardOption7.setOnClickListener { toggleCategory("Bilim", binding.cardOption7) }
    }

    private fun toggleCategory(
        category: String,
        cardView: View,
    ) {
        if (selectedCategories.contains(category)) {
            // Remove category if already selected
            selectedCategories.remove(category)
            cardView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white)) // Reset background color
        } else {
            if (selectedCategories.size < 3) {
                // Add category if not selected and limit not reached
                selectedCategories.add(category)
                cardView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selected_card_color))
            } else {
                // Show error message if limit reached
                Toast.makeText(requireContext(), "En fazla 3 kategori seçebilirsiniz.", Toast.LENGTH_SHORT).show()
            }
        }

        // Update the ViewModel with the selected categories
        viewModel.setSelectedCategories(selectedCategories)
    }

    private fun validateAndNavigate() {
        if (selectedCategories.size in 1..3) {
            navigateToNextFragment()
        } else {
            Toast.makeText(requireContext(), "En az 1 ve en fazla 3 kategori seçmelisiniz.", Toast.LENGTH_SHORT).show()
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
