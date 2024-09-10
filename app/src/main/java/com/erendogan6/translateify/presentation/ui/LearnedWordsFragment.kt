package com.erendogan6.translateify.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLearnedWordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
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
        val detailFragment = WordDetailFragment()
        val args = Bundle().apply { putParcelable("word", word) }
        detailFragment.arguments = args

        parentFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
