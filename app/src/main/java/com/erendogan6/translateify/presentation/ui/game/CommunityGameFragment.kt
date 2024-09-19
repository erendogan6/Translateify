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
        // Add a divider between items
        val dividerItemDecoration =
            androidx.recyclerview.widget.DividerItemDecoration(
                binding.recyclerView.context,
                (binding.recyclerView.layoutManager as LinearLayoutManager).orientation,
            )
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
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
                        "Son Kelime: $word"
                    } else {
                        "Oyun Başlıyor!"
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
                        "Sıra sizde"
                    } else {
                        "Diğer oyuncuları bekleyin"
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
                Toast.makeText(requireContext(), "Kelime boş olamaz!", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle "Enter" key press on the keyboard
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
                .setTitle("Oyun Kuralları")
                .setMessage(
                    "Bu oyunda amacınız, diğer oyuncuların girdiği son kelimenin son harfiyle başlayan yeni bir kelime girmektir.\n\n" +
                        "Kurallar:\n" +
                        "1. Girilen kelime son 20 kelime içinde olmamalıdır.\n" +
                        "2. Aynı kullanıcı art arda kelime giremez.\n" +
                        "3. Kelimeniz, önceki kelimenin son harfiyle başlamalıdır.\n\n" +
                        "İyi eğlenceler!",
                ).setPositiveButton("Tamam") { dialog, _ ->
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
