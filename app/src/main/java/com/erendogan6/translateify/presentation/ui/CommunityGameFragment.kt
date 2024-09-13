package com.erendogan6.translateify.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.erendogan6.translateify.databinding.FragmentCommunityGameBinding
import com.erendogan6.translateify.presentation.adapter.CommunityWordAdapter
import com.erendogan6.translateify.presentation.viewmodel.CommunityGameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommunityGameFragment : Fragment() {
    private var _binding: FragmentCommunityGameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommunityGameViewModel by viewModels()

    private lateinit var adapter: CommunityWordAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCommunityGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.btnSubmit.setOnClickListener {
            val userWord =
                binding.etUserWord.text
                    .toString()
                    .trim()
            if (userWord.isNotBlank()) {
                viewModel.onUserInput(userWord)
                binding.etUserWord.text?.clear()
            } else {
                Toast.makeText(requireContext(), "Kelime boş olamaz!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = CommunityWordAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.communityWords.collect { words ->
                adapter.submitList(words) {
                    binding.recyclerView.scrollToPosition(words.size - 1)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userScore.collect { score ->
                binding.tvScore.text = "Puan: $score"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentWord.collect { word ->
                binding.tvCurrentWord.text = word ?: "En son kelime yok"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
