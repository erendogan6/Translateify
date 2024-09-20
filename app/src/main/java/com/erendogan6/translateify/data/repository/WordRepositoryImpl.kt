package com.erendogan6.translateify.data.repository

import com.erendogan6.translateify.data.local.WordDao
import com.erendogan6.translateify.data.mapper.toDomainModel
import com.erendogan6.translateify.data.mapper.toEntity
import com.erendogan6.translateify.data.remote.GeminiService
import com.erendogan6.translateify.data.remote.PexelsService
import com.erendogan6.translateify.domain.model.Word
import com.erendogan6.translateify.domain.repository.WordRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class WordRepositoryImpl(
    private val wordDao: WordDao,
    private val geminiService: GeminiService,
    private val pexelsService: PexelsService,
    private val firestore: FirebaseFirestore,
) : WordRepository {
    override fun getRandomWords(): Flow<List<Word>> =
        wordDao.getRandomWords().map { entities ->
            entities.map { it.toDomainModel() }
        }

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

    override suspend fun getWordTranslation(word: String): String = geminiService.getTranslation(word)

    override suspend fun getWordImage(word: String): String? =
        try {
            val apiKey = Firebase.remoteConfig.getString("PEXELS_API_KEY")
            val response = pexelsService.searchPhotos(apiKey, word)

            if (response.photos.isNotEmpty()) {
                val imageUrl =
                    response.photos
                        .first()
                        .src.original
                imageUrl
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }

    override suspend fun fetchWordsFromFirebase(
        selectedCategories: List<String>,
        difficulty: String?,
    ): Flow<List<Word>> {
        // Step 1: Check local Room data first
        val localWords = wordDao.getRandomWords().firstOrNull()
        if (!localWords.isNullOrEmpty()) {
            return wordDao.getRandomWords().map { entities -> entities.map { it.toDomainModel() } }
        }

        // Step 2: Fetch from Firestore if Room data is not available
        try {
            val query = firestore.collection("words")

            if (selectedCategories.isNotEmpty()) {
                query.whereArrayContainsAny("categories", selectedCategories)
            }
            if (!difficulty.isNullOrEmpty()) {
                query.whereEqualTo("difficulty", difficulty)
            }

            val snapshot = query.limit(120).get().await()

            val wordsList =
                snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val id = doc.id
                    val english = data["english"] as? String ?: ""
                    val translation = data["translation"] as? String ?: ""
                    val isLearned = data["isLearned"] as? Boolean ?: false
                    val difficult = data["difficulty"] as? String ?: ""
                    val categories = (data["categories"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                    Word(
                        id = id,
                        english = english,
                        translation = translation,
                        isLearned = isLearned,
                        difficulty = difficult,
                        categories = categories,
                    )
                }

            // Save to local database
            wordDao.insertWords(wordsList.map { it.toEntity() })

            return getRandomWords() // Return the words from Room
        } catch (e: Exception) {
            Timber.e("Error fetching words from Firestore: ${e.message}")
            throw e
        }
    }

    override suspend fun getRandomWord(): Word = wordDao.getRandomWord()
}
