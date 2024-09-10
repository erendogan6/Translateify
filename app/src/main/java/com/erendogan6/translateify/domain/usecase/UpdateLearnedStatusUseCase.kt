package com.erendogan6.translateify.domain.usecase

import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.domain.repository.WordRepository
import javax.inject.Inject

class UpdateLearnedStatusUseCase
    @Inject
    constructor(
        private val wordRepository: WordRepository,
    ) {
        suspend operator fun invoke(word: Word) {
            wordRepository.updateLearnedStatus(word)
        }
    }
