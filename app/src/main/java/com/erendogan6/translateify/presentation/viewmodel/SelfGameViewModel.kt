package com.erendogan6.translateify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelfGameViewModel
    @Inject
    constructor(
        private val wordRepository: WordRepository,
    ) : ViewModel() {
        private val _currentWord = MutableStateFlow<Word?>(null)
        val currentWord: StateFlow<Word?> = _currentWord

        private val _usedWords = MutableStateFlow<List<String>>(emptyList())
        val usedWords: StateFlow<List<String>> = _usedWords

        private val _score = MutableStateFlow(0)
        val score: StateFlow<Int> = _score

        private val _gameOver = MutableStateFlow(false)
        val gameOver: StateFlow<Boolean> = _gameOver

        fun startGame() {
            viewModelScope.launch {
                _usedWords.value = emptyList()
                _score.value = 0
                _gameOver.value = false
                fetchRandomWord()
            }
        }

        private fun fetchRandomWord() {
            viewModelScope.launch {
                val word = wordRepository.getRandomWord()
                _currentWord.value = word
            }
        }

        fun onUserInput(userWord: String) {
            if (isValidWord(userWord)) {
                _usedWords.value = _usedWords.value + userWord
                _score.value += 1
                fetchNextWord()
            } else {
                _gameOver.value = true
            }
        }

        private fun isValidWord(word: String): Boolean {
            val currentWord = _currentWord.value?.english ?: return false
            val lastChar = currentWord.lastOrNull()?.lowercaseChar() ?: return false
            val firstChar = word.firstOrNull()?.lowercaseChar() ?: return false

            val isNotUsed = !_usedWords.value.contains(word)

            val isCorrectStartingLetter = firstChar == lastChar

            return isNotUsed && isCorrectStartingLetter
        }

        private fun fetchNextWord() {
            viewModelScope.launch {
                val word = wordRepository.getRandomWord()
                _currentWord.value = word
            }
        }
    }
