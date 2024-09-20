package com.erendogan6.translateify.presentation.ui.word

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentAddWordBinding
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.presentation.viewmodel.RandomWordsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddWordFragment : Fragment(R.layout.fragment_add_word) {
    private var _binding: FragmentAddWordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RandomWordsViewModel by viewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentAddWordBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddWord.setOnClickListener {
            val englishWord = binding.etEnglishWord.text.toString()
            val translation = binding.etTranslation.text.toString()

            if (englishWord.isNotBlank() && translation.isNotBlank()) {
                val newWord =
                    Word(
                        english = englishWord,
                        translation = translation,
                        isLearned = false,
                        difficulty = "easy",
                        categories = emptyList(),
                    )
                viewModel.addWord(newWord)
                Toast.makeText(requireContext(), "Word added successfully!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
