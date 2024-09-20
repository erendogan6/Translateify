package com.erendogan6.translateify.presentation.ui.word

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.erendogan6.translateify.R
import com.erendogan6.translateify.databinding.FragmentRandomWordsBinding
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.presentation.adapter.WordAdapter
import com.erendogan6.translateify.presentation.state.UiState
import com.erendogan6.translateify.presentation.viewmodel.RandomWordsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RandomWordsFragment : Fragment(R.layout.fragment_random_words) {
    private val viewModel: RandomWordsViewModel by activityViewModels()
    private var _binding: FragmentRandomWordsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WordAdapter
    private var isSwipeToRefresh: Boolean = false

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        _binding = FragmentRandomWordsBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeUiState()
        observeWords()
        handleSwipeToRefresh()

        if (viewModel.words.value.isEmpty()) {
            fetchUserPreferencesAndLoadWords()
        }
    }

    private fun setupUI() {
        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter =
            WordAdapter(
                onItemClick = ::navigateToDetail,
                onLearnedClick = viewModel::toggleLearnedStatus,
            )
        binding.recyclerView.adapter = adapter

        // Setup Lottie Animation
        binding.lottieAnimationView.setAnimation(R.raw.loading)
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UiState.Loading -> showLoading(true)
                        is UiState.Success -> {
                            showLoading(false)
                            updateWordList(uiState.words)
                        }
                        is UiState.Error -> {
                            showLoading(false)
                            showError(uiState.message)
                        }
                        else -> {
                            Timber.d("Idle state")
                        }
                    }
                }
            }
        }
    }

    private fun observeWords() {
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

    private fun handleSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            isSwipeToRefresh = true
            viewModel.shuffleWords()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.lottieAnimationView.apply {
            visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) playAnimation() else cancelAnimation()
        }
        binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun updateWordList(words: List<Word>) {
        adapter.submitList(words) {
            if (isSwipeToRefresh) {
                binding.recyclerView.scrollToPosition(0)
                isSwipeToRefresh = false
            }
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun fetchUserPreferencesAndLoadWords() {
        Timber.d("Fetching user preferences and loading words")
        val user = firebaseAuth.currentUser
        val userId = user?.uid ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val document =
                    firestore
                        .collection("users")
                        .document(userId)
                        .get()
                        .await()

                val interests = document["interests"] as? List<*> ?: emptyList<String>()
                val difficulty = document.getString("difficulty")
                viewModel.fetchWordsFromFirebase(interests.filterIsInstance<String>(), difficulty)
            } catch (e: Exception) {
                Timber.e("Error fetching user preferences: ${e.message}")
                viewModel.fetchWordsFromFirebase(emptyList(), null)
            }
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
