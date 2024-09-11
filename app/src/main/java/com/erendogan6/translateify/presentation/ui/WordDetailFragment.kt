package com.erendogan6.translateify.presentation.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentWordDetailBinding
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.presentation.viewmodel.RandomWordsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WordDetailFragment : Fragment(R.layout.fragment_word_detail) {
    private var _binding: FragmentWordDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RandomWordsViewModel by viewModels()
    private var word: Word? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        word =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable("word", Word::class.java)
            } else {
                arguments?.getParcelable("word")
            }

        word?.let { word ->
            binding.tvWordDetail.text = word.english
            binding.tvTranslationDetail.text = word.translation

            binding.btnToggleLearned.text =
                if (word.isLearned) getString(R.string.unlearn_word) else getString(R.string.learn_word)

            binding.btnToggleLearned.setOnClickListener {
                viewModel.toggleLearnedStatus(word)
                findNavController().popBackStack()
            }

            binding.btnPronounce.setOnClickListener {
                pronounceWord(word)
            }
        }
    }

    private fun pronounceWord(word: Word) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
