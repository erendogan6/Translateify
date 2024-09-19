package com.erendogan6.translateify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.domain.usecase.AddWordUseCase
import com.erendogan6.translateify.domain.usecase.GetWordImageUseCase
import com.erendogan6.translateify.domain.usecase.GetWordTranslationUseCase
import com.erendogan6.translateify.domain.usecase.LoadWordsUseCase
import com.erendogan6.translateify.domain.usecase.UpdateLearnedStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RandomWordsViewModel
    @Inject
    constructor(
        private val updateLearnedStatusUseCase: UpdateLearnedStatusUseCase,
        private val addWordUseCase: AddWordUseCase,
        private val getWordTranslationUseCase: GetWordTranslationUseCase,
        private val getWordImageUseCase: GetWordImageUseCase,
        private val loadWordsUseCase: LoadWordsUseCase,
    ) : ViewModel() {
        private val _words = MutableStateFlow<List<Word>>(emptyList())
        val words: StateFlow<List<Word>> = _words

        private val _translation = MutableStateFlow<String?>(null)
        val translation: StateFlow<String?> get() = _translation

        private val _imageUrl = MutableStateFlow<String?>(null)
        val imageUrl: StateFlow<String?> get() = _imageUrl

        private var isDataLoadedFromFirebase = false

        fun setTranslationReset() {
            _translation.value = null
        }

        fun fetchWordsFromFirebase(
            selectedCategories: List<String>,
            difficulty: String?,
        ) {
            viewModelScope.launch {
                try {
                    loadWordsUseCase(selectedCategories, difficulty).collect { wordList ->
                        if (_words.value.isEmpty() && !isDataLoadedFromFirebase) {
                            _words.value = wordList
                            isDataLoadedFromFirebase = true
                            Timber.d("Loaded words: $wordList")
                        }
                    }
                } catch (e: Exception) {
                    Timber.e("Error loading words: " + e.message)
                }
            }
        }

        fun addWord(word: Word) {
            viewModelScope.launch {
                addWordUseCase(word)
            }
        }

        fun fetchTranslation(word: String) {
            viewModelScope.launch {
                _translation.value = getWordTranslationUseCase(word)
            }
        }

        fun fetchWordImage(word: String) {
            viewModelScope.launch {
                try {
                    val imageUrl = getWordImageUseCase(word)
                    _imageUrl.value = imageUrl
                } catch (e: Exception) {
                    Timber.e("Error fetching image: " + e.message)
                }
            }
        }

        fun shuffleWords() {
            _words.value = _words.value.shuffled()
            Timber.d("Shuffled words: " + _words.value)
        }

        fun toggleLearnedStatus(word: Word) {
            viewModelScope.launch {
                val updatedWord = word.copy(isLearned = !word.isLearned)
                updateLearnedStatusUseCase(updatedWord)

                _words.value =
                    if (updatedWord.isLearned) {
                        _words.value.filter { it.id != updatedWord.id }
                    } else {
                        _words.value + updatedWord
                    }

                Timber.d("Updated learned status for word: " + updatedWord.id)
            }
        }
    }
