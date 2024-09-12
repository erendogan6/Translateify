package com.erendogan6.translateify.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.erendogan6.translateify.databinding.FragmentSelfGameBinding
import com.erendogan6.translateify.presentation.viewmodel.SelfGameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelfGameFragment : Fragment() {
    private var _binding: FragmentSelfGameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SelfGameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSelfGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Oyunu başlat
        viewModel.startGame()

        // Kullanıcı kelime girişi
        binding.btnSubmit.setOnClickListener {
            val userWord =
                binding.etUserWord.text
                    .toString()
                    .trim()
            if (userWord.isNotBlank()) {
                viewModel.onUserInput(userWord)
                binding.etUserWord.text.clear()
            } else {
                Toast.makeText(requireContext(), "Kelime boş olamaz!", Toast.LENGTH_SHORT).show()
            }
        }

        // Şu anki kelimeyi göster
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentWord.collect { word ->
                binding.tvCurrentWord.text = word?.english ?: "Bilgisayar kelime veriyor..."
            }
        }

        // Puanı göster
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.score.collect { score ->
                binding.tvScore.text = "Puan: $score"
            }
        }

        // Kullanılmış kelimeleri göster
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.usedWords.collect { usedWords ->
                binding.tvUsedWords.text = "Kullanılan kelimeler: ${usedWords.joinToString(", ")}"
            }
        }

        // Oyun bittiğinde uyarı göster
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.gameOver.collect { isOver ->
                if (isOver) {
                    Toast.makeText(requireContext(), "Oyun bitti!", Toast.LENGTH_SHORT).show()
                    binding.btnSubmit.isEnabled = false // Oyuncunun daha fazla kelime girmesini engelle
                }
            }
        }

        // Geri sayımı göster
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.timeLeft.collect { timeLeft ->
                binding.tvTimer.text = "Kalan süre: $timeLeft saniye"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
