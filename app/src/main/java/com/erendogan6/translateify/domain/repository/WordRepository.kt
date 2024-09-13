package com.erendogan6.translateify.domain.repository

import com.erendogan6.translateify.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    suspend fun addWord(word: Word)

    fun getRandomWords(): Flow<List<Word>>

    fun getLearnedWords(): Flow<List<Word>>

    suspend fun updateLearnedStatus(word: Word)

    suspend fun getWordTranslation(word: String): String

    suspend fun getWordImage(word: String): String?

    suspend fun fetchWordsFromFirebase(
        selectedCategories: List<String>,
        difficulty: String?,
    ): Flow<List<Word>>

    suspend fun getRandomWord(): Word

    suspend fun saveUserToFirebase(
        email: String,
        name: String,
        level: String,
        interests: List<String>,
    )
}
