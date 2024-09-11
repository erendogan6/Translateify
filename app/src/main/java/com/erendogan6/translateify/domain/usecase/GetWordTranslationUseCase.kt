package com.erendogan6.translateify.domain.usecase

import com.erendogan6.translateify.domain.repository.WordRepository
import javax.inject.Inject

class GetWordTranslationUseCase
    @Inject
    constructor(
        private val wordRepository: WordRepository,
    ) {
        suspend operator fun invoke(word: String): String = wordRepository.getWordTranslation(word)
    }
