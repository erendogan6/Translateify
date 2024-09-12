package com.erendogan6.translateify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.erendogan6.translateify.domain.model.CommunityWord
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CommunityGameViewModel
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) : ViewModel() {
        private val _currentWord = MutableStateFlow<String?>(null)
        val currentWord: StateFlow<String?> = _currentWord

        private val _usedWords = MutableStateFlow<List<String>>(emptyList())
        val usedWords: StateFlow<List<String>> = _usedWords

        private val _userScore = MutableStateFlow(0)
        val userScore: StateFlow<Int> = _userScore

        private val _communityWords = MutableStateFlow<List<CommunityWord>>(emptyList())
        val communityWords: StateFlow<List<CommunityWord>> = _communityWords

        private val _gameOver = MutableStateFlow(false)
        val gameOver: StateFlow<Boolean> = _gameOver

        private val userId = FirebaseAuth.getInstance().currentUser?.uid

        private var listenerRegistration: ListenerRegistration? = null

        init {
            listenForLatestWordUpdates()
            observeCommunityWords()
            observeUserScore()
        }

        private fun listenForLatestWordUpdates() {
            listenerRegistration =
                firestore
                    .collection("community_words")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            return@addSnapshotListener
                        }

                        if (snapshot != null && !snapshot.isEmpty) {
                            val latestWord = snapshot.documents.firstOrNull()?.getString("word")
                            _currentWord.value = latestWord
                        }
                    }
        }

        fun onUserInput(userWord: String) {
            if (isValidWord(userWord)) {
                addWordToCommunity(userWord)
            } else {
                _gameOver.value = true
            }
        }

        private fun isValidWord(word: String): Boolean {
            val currentWord = _communityWords.value.lastOrNull()?.word ?: return false
            val lastChar = currentWord.lastOrNull()?.lowercaseChar() ?: return false
            val firstChar = word.firstOrNull()?.lowercaseChar() ?: return false

            return firstChar == lastChar && !_communityWords.value.any { it.word == word }
        }

        private fun addWordToCommunity(word: String) {
            val newWord =
                hashMapOf(
                    "word" to word,
                    "userId" to userId,
                    "timestamp" to
                        Timestamp
                            .now(),
                )

            firestore
                .collection("community_words")
                .add(newWord)
                .addOnSuccessListener {
                    _userScore.value += 1
                    updateUserScore()
                }.addOnFailureListener {
                    _gameOver.value = true
                }
        }

        private fun updateUserScore() {
            userId?.let {
                firestore
                    .collection("users")
                    .document(it)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val score = snapshot.getLong("score") ?: 0
                        firestore
                            .collection("users")
                            .document(it)
                            .update("score", score + 1)
                    }
            }
        }

        private fun observeUserScore() {
            userId?.let { uid ->
                firestore
                    .collection("users")
                    .document(uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }

                        if (snapshot != null && snapshot.exists()) {
                            val score = snapshot.getLong("score") ?: 0
                            _userScore.value = score.toInt()
                        }
                    }
            }
        }

        private fun observeCommunityWords() {
            firestore
                .collection("community_words")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val words = mutableListOf<CommunityWord>()
                        val documents = snapshot.documents

                        documents.forEach { doc ->
                            val word = doc.getString("word") ?: return@forEach
                            val userId = doc.getString("userId") ?: return@forEach
                            val timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: return@forEach

                            fetchUserInfo(userId) { userEmail ->
                                val communityWord = CommunityWord(word, userEmail, timestamp)
                                words.add(communityWord)

                                if (words.size == documents.size) {
                                    _communityWords.value = words
                                }
                            }
                        }
                    }
                }
        }

        private fun fetchUserInfo(
            userId: String,
            callback: (String) -> Unit,
        ) {
            firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val email = document.getString("email") ?: "Bilinmeyen Kullanıcı"
                    callback(email)
                }.addOnFailureListener {
                    callback("Bilinmeyen Kullanıcı")
                }
        }

        override fun onCleared() {
            super.onCleared()
            listenerRegistration?.remove()
        }
    }
