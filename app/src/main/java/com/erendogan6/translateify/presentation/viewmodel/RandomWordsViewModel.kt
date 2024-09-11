package com.erendogan6.translateify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.domain.usecase.GetRandomWordsUseCase
import com.erendogan6.translateify.domain.usecase.UpdateLearnedStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RandomWordsViewModel
    @Inject
    constructor(
        private val getRandomWordsUseCase: GetRandomWordsUseCase,
        private val updateLearnedStatusUseCase: UpdateLearnedStatusUseCase,
        private val addWordUseCase: AddWordUseCase,
    ) : ViewModel() {
        private val _words = MutableStateFlow<List<Word>>(emptyList())
        val words: StateFlow<List<Word>> = _words

        init {
            loadRandomWords()
        }

        fun addWord(word: Word) {
            viewModelScope.launch {
                addWordUseCase(word)
            }
        }
        fun loadRandomWords(shuffle: Boolean = false) {
            viewModelScope.launch {
                getRandomWordsUseCase().collect { wordList ->
                    _words.value = if (shuffle) wordList.shuffled() else wordList
                }
            }
        }

        fun toggleLearnedStatus(word: Word) {
            viewModelScope.launch {
                val updatedWord = word.copy(isLearned = !word.isLearned)
                updateLearnedStatusUseCase(updatedWord)

                _words.value =
                    _words.value.map {
                        if (it.id == updatedWord.id) updatedWord else it
                    }
            }
        }
    }
