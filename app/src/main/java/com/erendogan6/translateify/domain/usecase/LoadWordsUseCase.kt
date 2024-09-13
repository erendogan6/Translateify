package com.erendogan6.translateify.domain.usecase

import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadWordsUseCase
    @Inject
    constructor(
        private val wordRepository: WordRepository,
    ) {
        suspend operator fun invoke(
            selectedCategories: List<String>,
            difficulty: String?,
        ): Flow<List<Word>> = wordRepository.fetchWordsFromFirebase(selectedCategories, difficulty)
    }
