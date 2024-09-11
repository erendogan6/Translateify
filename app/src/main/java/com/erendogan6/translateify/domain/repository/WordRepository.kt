package com.erendogan6.translateify.domain.repository

import com.erendogan6.translateify.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    suspend fun addWord(word: Word)

    fun getRandomWords(): Flow<List<Word>>

    fun getLearnedWords(): Flow<List<Word>>

    suspend fun updateLearnedStatus(word: Word)

    suspend fun getWordTranslation(word: String): String

}
