package com.erendogan6.translateify.domain.usecase

import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.domain.repository.WordRepository
import javax.inject.Inject

class AddWordUseCase
    @Inject
    constructor(
        private val repository: WordRepository,
    ) {
        suspend operator fun invoke(word: Word) {
            repository.addWord(word)
        }
    }
