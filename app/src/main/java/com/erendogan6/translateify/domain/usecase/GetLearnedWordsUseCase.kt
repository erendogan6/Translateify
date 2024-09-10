package com.erendogan6.translateify.domain.usecase

import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLearnedWordsUseCase
    @Inject
    constructor(
        private val wordRepository: WordRepository,
    ) {
        operator fun invoke(): Flow<List<Word>> = wordRepository.getLearnedWords()
    }
