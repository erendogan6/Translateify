package com.erendogan6.translateify.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.erendogan6.translateify.data.model.WordEntity
import com.erendogan6.translateify.domain.model.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Query("SELECT * FROM words WHERE isLearned = 0 LIMIT :limit")
    fun getRandomWords(limit: Int = 120): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE isLearned = 1")
    fun getLearnedWords(): Flow<List<WordEntity>>

    @Update
    suspend fun updateWord(word: WordEntity)

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): Word
}
