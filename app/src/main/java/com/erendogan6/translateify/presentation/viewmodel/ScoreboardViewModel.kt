package com.erendogan6.translateify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.erendogan6.translateify.domain.model.UserScore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ScoreboardViewModel
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) : ViewModel() {
        private val _userScores = MutableStateFlow<List<UserScore>>(emptyList())
        val userScores: StateFlow<List<UserScore>> = _userScores

        private var listenerRegistration: ListenerRegistration? = null

        init {
            observeUserScores()
        }

        private fun observeUserScores() {
            listenerRegistration =
                firestore
                    .collection("users")
                    .orderBy("score", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }

                        if (snapshot != null) {
                            val scores = mutableListOf<UserScore>()
                            for (document in snapshot.documents) {
                                val userId = document.id
                                val score = document.getLong("score")?.toInt() ?: 0
                                val email = document.getString("email") ?: "Bilinmeyen Kullanıcı"
                                scores.add(UserScore(userId, email, score))
                            }
                            _userScores.value = scores
                        }
                    }
        }

        override fun onCleared() {
            super.onCleared()
            listenerRegistration?.remove()
        }
    }
