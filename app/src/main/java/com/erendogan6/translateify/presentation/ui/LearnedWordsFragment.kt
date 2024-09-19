package com.erendogan6.translateify.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentLearnedWordsBinding
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.presentation.adapter.WordAdapter
import com.erendogan6.translateify.presentation.viewmodel.LearnedWordsViewModel
import com.erendogan6.translateify.presentation.viewmodel.RandomWordsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LearnedWordsFragment : Fragment(R.layout.fragment_learned_words) {
    private var _binding: FragmentLearnedWordsBinding? = null
    private val binding get() = _binding!!
    private val learnedViewModel: LearnedWordsViewModel by viewModels()
    private val randomViewModel: RandomWordsViewModel by viewModels()
    private lateinit var adapter: WordAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentLearnedWordsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewLearnedWords.layoutManager = layoutManager

        adapter =
            WordAdapter(
                onItemClick = { word -> navigateToDetail(word) },
                onLearnedClick = { word -> randomViewModel.toggleLearnedStatus(word) },
            )
        binding.recyclerViewLearnedWords.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            learnedViewModel.learnedWords.collect { learnedWords ->
                adapter.submitList(learnedWords)
            }
        }
    }

    private fun navigateToDetail(word: Word) {
        val action = LearnedWordsFragmentDirections.actionLearnedWordsFragmentToWordDetailFragment(word)
        requireView().findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
