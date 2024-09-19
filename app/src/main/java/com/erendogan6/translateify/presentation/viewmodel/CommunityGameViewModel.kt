package com.erendogan6.translateify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.translateify.domain.model.CommunityWord
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityGameViewModel
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) : ViewModel() {
        private val _currentWord = MutableStateFlow<String?>(null)
        val currentWord: StateFlow<String?> = _currentWord.asStateFlow()

        private val _communityWords = MutableStateFlow<List<CommunityWord>>(emptyList())
        val communityWords: StateFlow<List<CommunityWord>> = _communityWords.asStateFlow()

        private val _userScore = MutableStateFlow(0)
        val userScore: StateFlow<Int> = _userScore.asStateFlow()

        private val _errorMessage = MutableSharedFlow<String?>()
        val errorMessage: SharedFlow<String?> = _errorMessage.asSharedFlow()

        private val _isUserTurn = MutableStateFlow(true)
        val isUserTurn: StateFlow<Boolean> = _isUserTurn.asStateFlow()

        private var _notificationShown = false
        val notificationShown: Boolean
            get() = _notificationShown

        fun setNotificationShown() {
            _notificationShown = true
        }

        private val userId = FirebaseAuth.getInstance().currentUser?.uid

        private var listenerRegistration: ListenerRegistration? = null

        init {
            listenForCommunityWords()
            observeUserScore()
        }

        private fun listenForCommunityWords() {
            listenerRegistration =
                firestore
                    .collection("community_words")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(20)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            // Handle error
                            return@addSnapshotListener
                        }

                        if (snapshot != null) {
                            val words =
                                snapshot.documents
                                    .mapNotNull { doc ->
                                        val word = doc.getString("word") ?: return@mapNotNull null
                                        val userId = doc.getString("userId") ?: return@mapNotNull null
                                        val timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: return@mapNotNull null
                                        CommunityWord(word, "", timestamp, userId)
                                    }.reversed() // Reverse to have oldest first

                            _communityWords.value = words
                            _currentWord.value = words.lastOrNull()?.word

                            updateIsUserTurn()
                            fetchUserEmails(words)
                        }
                    }
        }

        private fun fetchUserEmails(words: List<CommunityWord>) {
            words.forEachIndexed { index, communityWord ->
                fetchUserInfo(communityWord.userId) { username ->
                    val updatedWord = communityWord.copy(username = username)
                    _communityWords.value =
                        _communityWords.value.toMutableList().apply {
                            this[index] = updatedWord
                        }
                }
            }
        }

        fun onUserInput(userWord: String) {
            if (userId == null) {
                // Handle user not logged in
                return
            }

            if (isValidWord(userWord)) {
                addWordToCommunity(userWord)
            }
        }

        private fun isValidWord(word: String): Boolean {
            val communityWords = _communityWords.value

            // Check if the word has been used in the last 20 words
            if (communityWords.any { it.word.equals(word, ignoreCase = true) }) {
                viewModelScope.launch {
                    _errorMessage.emit("Bu kelime zaten kullanıldı.")
                }
                return false
            }

            // Check if the user is trying to play twice in a row
            val lastWord = communityWords.lastOrNull()
            if (lastWord != null && lastWord.userId == userId) {
                viewModelScope.launch {
                    _errorMessage.emit("Sıra sizde değil.")
                }
                return false
            }

            // If there is a current word, check if the first letter matches the last letter
            val currentWord = lastWord?.word
            if (currentWord != null) {
                val lastChar = currentWord.lastOrNull()?.lowercaseChar()
                val firstChar = word.firstOrNull()?.lowercaseChar()
                if (lastChar != firstChar) {
                    viewModelScope.launch {
                        _errorMessage.emit("Kelime '$currentWord' kelimesinin son harfi ile başlamalı.")
                    }
                    return false
                }
            }

            // All checks passed
            return true
        }

        private fun addWordToCommunity(word: String) {
            if (userId == null) return

            val newWord =
                hashMapOf(
                    "word" to word,
                    "userId" to userId,
                    "timestamp" to Timestamp.now(),
                )

            firestore
                .collection("community_words")
                .add(newWord)
                .addOnSuccessListener {
                    _userScore.value += 1
                    updateUserScore()
                    // Reset error message
                    viewModelScope.launch {
                        _errorMessage.emit(null)
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _errorMessage.emit("Kelime eklenirken bir hata oluştu.")
                    }
                }
        }

        private fun updateUserScore() {
            userId?.let {
                firestore
                    .collection("users")
                    .document(it)
                    .update("score", _userScore.value)
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

        private fun updateIsUserTurn() {
            val lastWordUserId = _communityWords.value.lastOrNull()?.userId
            _isUserTurn.value = (lastWordUserId != userId)
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
