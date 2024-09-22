package com.erendogan6.translateify.presentation.ui.game

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentCommunityGameBinding
import com.erendogan6.translateify.presentation.adapter.CommunityWordAdapter
import com.erendogan6.translateify.presentation.viewmodel.CommunityGameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommunityGameFragment : Fragment(R.layout.fragment_community_game) {
    private var _binding: FragmentCommunityGameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CommunityGameViewModel by activityViewModels()

    private lateinit var adapter: CommunityWordAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentCommunityGameBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupListeners()
        showGameRulesDialog()
    }

    private fun setupRecyclerView() {
        adapter = CommunityWordAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.communityWords.collectLatest { words ->
                adapter.submitList(words) {
                    binding.recyclerView.scrollToPosition(words.size - 1)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userScore.collect { score ->
                binding.tvScore.text =
                    getString(
                        R.string.puan,
                        score.toString(),
                    )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentWord.collect { word ->
                binding.tvCurrentWord.text =
                    if (word != null) {
                        getString(R.string.lastWord) + " " + word
                    } else {
                        getString(R.string.oyun_basliyor)
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collect { errorMessage ->
                errorMessage?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isUserTurn.collect { isUserTurn ->
                binding.btnSubmit.isEnabled = isUserTurn
                binding.etUserWord.isEnabled = isUserTurn
                binding.tvTurnIndicator.text =
                    if (isUserTurn) {
                        getString(R.string.sira_sizde)
                    } else {
                        getString(R.string.diger_oyunculari_bekleyin)
                    }
            }
        }
    }

    private fun setupListeners() {
        binding.btnSubmit.setOnClickListener {
            val userWord =
                binding.etUserWord.text
                    .toString()
                    .trim()
            if (userWord.isNotBlank()) {
                viewModel.onUserInput(userWord)
                binding.etUserWord.text?.clear()
            } else {
                Toast
                    .makeText(
                        requireContext(),
                        getString(R.string.kelime_bos_olamaz),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

        binding.etUserWord.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnSubmit.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun showGameRulesDialog() {
        if (!viewModel.notificationShown) {
            AlertDialog
                .Builder(requireContext())
                .setTitle(getString(R.string.oyun_kurallari))
                .setMessage(
                    getString(R.string.game_rules),
                ).setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
            viewModel.setNotificationShown()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
