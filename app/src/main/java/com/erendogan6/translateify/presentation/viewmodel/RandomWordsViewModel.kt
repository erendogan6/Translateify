package com.erendogan6.translateify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.translateify.R
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.domain.usecase.AddWordUseCase
import com.erendogan6.translateify.domain.usecase.GetWordImageUseCase
import com.erendogan6.translateify.domain.usecase.GetWordTranslationUseCase
import com.erendogan6.translateify.domain.usecase.LoadWordsUseCase
import com.erendogan6.translateify.domain.usecase.UpdateLearnedStatusUseCase
import com.erendogan6.translateify.presentation.state.UiState
import com.erendogan6.translateify.utils.StringProvider
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
        private val stringProvider: StringProvider,
    ) : ViewModel() {
        private val _words = MutableStateFlow<List<Word>>(emptyList())
        val words: StateFlow<List<Word>> = _words

        private val _renderedHtml = MutableStateFlow<String?>(null)
        val renderedHtml: StateFlow<String?> get() = _renderedHtml

        private val _imageUrl = MutableStateFlow<String?>(null)
        val imageUrl: StateFlow<String?> get() = _imageUrl

        private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
        val uiState: StateFlow<UiState> get() = _uiState

        private var isDataLoadedFromFirebase = false

         var fetchIsProcessing = false
            private set

        fun fetchWordsFromFirebase(
            selectedCategories: List<String>,
            difficulty: String?,
        ) {
            viewModelScope.launch {
                _uiState.value = UiState.Loading
                try {
                    loadWordsUseCase(selectedCategories, difficulty).collect { wordList ->
                        if (_words.value.isEmpty() && !isDataLoadedFromFirebase) {
                            _words.value = wordList
                            isDataLoadedFromFirebase = true
                            _uiState.value = UiState.Success(wordList)
                            Timber.d("Loaded words: $wordList")
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value =
                        e.message
                            ?.let {
                                stringProvider.getString(
                                    R.string.error_loading_words,
                                    it,
                                )
                            }?.let {
                                UiState.Error(it)
                            }!!
                    Timber.e("Error loading words: ${e.message}")
                }
            }
        }

        fun addWord(word: Word) {
            viewModelScope.launch {
                addWordUseCase(word)
            }
        }

        fun fetchTranslation(word: String) {
            if (fetchIsProcessing) {
                return
            }
            fetchIsProcessing = true
            viewModelScope.launch {
                try {
                    val markdownText = getWordTranslationUseCase(word)

                    _renderedHtml.value = markdownText
                } catch (e: Exception) {
                    _renderedHtml.value =
                        stringProvider.getString(R.string.ceviri_basarisiz)
                } finally {
                    fetchIsProcessing = false
                }
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
