package com.erendogan6.translateify.presentation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentRandomWordsBinding
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.presentation.adapter.WordAdapter
import com.erendogan6.translateify.presentation.viewmodel.RandomWordsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RandomWordsFragment : Fragment(R.layout.fragment_random_words) {
    private val viewModel: RandomWordsViewModel by activityViewModels()
    private var _binding: FragmentRandomWordsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WordAdapter
    private var isSwipeToRefresh: Boolean = false
    private var isDataLoaded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRandomWordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager

        adapter =
            WordAdapter(
                onItemClick = { word -> navigateToDetail(word) },
                onLearnedClick = { word ->
                    viewModel.toggleLearnedStatus(word)
                },
            )

        if (viewModel.words.value.isEmpty() && !isDataLoaded) {
            fetchUserPreferencesAndLoadWords()
        }

        binding.recyclerView.adapter = adapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            isSwipeToRefresh = true
            viewModel.shuffleWords()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.words.collect { words ->
                adapter.submitList(words) {
                    if (isSwipeToRefresh) {
                        binding.recyclerView.scrollToPosition(0)
                        isSwipeToRefresh = false
                    }
                }
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun fetchUserPreferencesAndLoadWords() {
        Log.d("RandomWordsFragment", "Fetching user preferences and loading words")
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        firestore
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    Log.d("RandomWordsFragment", "DocumentSnapshot data: ${document.data}")
                    val interests = document.get("interests") as? List<String> ?: emptyList()
                    val difficulty = document.getString("difficulty")
                    viewModel.fetchWordsFromFirebase(interests, difficulty)
                    isDataLoaded = true
                } else {
                    viewModel.fetchWordsFromFirebase(emptyList(), null)
                    isDataLoaded = true
                }
            }.addOnFailureListener { exception ->
                viewModel.fetchWordsFromFirebase(emptyList(), null)
                isDataLoaded = true
                Log.e("RandomWordsFragment", "Error fetching user preferences: ${exception.message}")
            }
    }

    private fun navigateToDetail(word: Word) {
        val action = RandomWordsFragmentDirections.actionRandomWordsFragmentToWordDetailFragment(word)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
