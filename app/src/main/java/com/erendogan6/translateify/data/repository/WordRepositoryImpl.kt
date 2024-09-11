package com.erendogan6.translateify.data.repository

import com.erendogan6.translateify.data.local.WordDao
import com.erendogan6.translateify.data.mapper.toDomainModel
import com.erendogan6.translateify.data.mapper.toEntity
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WordRepositoryImpl(
    private val wordDao: WordDao,
) : WordRepository {
    override fun getRandomWords(): Flow<List<Word>> =
        wordDao
            .getRandomWords()
            .map { entities -> entities.map { it.toDomainModel() } }

    override fun getLearnedWords(): Flow<List<Word>> =
        wordDao
            .getLearnedWords()
            .map { entities -> entities.map { it.toDomainModel() } }

    override suspend fun updateLearnedStatus(word: Word) {
        wordDao.updateWord(word.toEntity())
    }

    override suspend fun addWord(word: Word) {
        wordDao.insertWord(word.toEntity())
    }

}
