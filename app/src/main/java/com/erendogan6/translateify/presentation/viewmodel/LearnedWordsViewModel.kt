package com.erendogan6.translateify.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.domain.usecase.GetLearnedWordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LearnedWordsViewModel
    @Inject
    constructor(
        getLearnedWordsUseCase: GetLearnedWordsUseCase,
    ) : ViewModel() {
        val learnedWords: StateFlow<List<Word>> =
            getLearnedWordsUseCase().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }
