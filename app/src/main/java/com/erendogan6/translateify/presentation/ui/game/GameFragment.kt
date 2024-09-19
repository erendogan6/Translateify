package com.erendogan6.translateify.presentation.ui.game

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentGameBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        binding.btnCommunityGame.setOnClickListener {
            findNavController().navigate(R.id.action_gameFragment_to_communityGameFragment)
        }

        binding.btnSelfGame.setOnClickListener {
            findNavController().navigate(R.id.action_gameFragment_to_selfGameFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
