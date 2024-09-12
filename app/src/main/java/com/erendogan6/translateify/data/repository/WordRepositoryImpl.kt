package com.erendogan6.translateify.data.repository

import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class WordRepositoryImpl(
    private val wordDao: WordDao,
    private val geminiService: GeminiService,
    private val pexelsService: PexelsService,
    private val firestore: FirebaseFirestore,
) : WordRepository {
    private val wordsFlow = MutableStateFlow<List<Word>>(emptyList())

    override fun getRandomWords(): Flow<List<Word>> = wordsFlow

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

    override suspend fun fetchWordsFromFirebase(): Flow<List<Word>> {
        try {
            val snapshot =
                firestore
                    .collection("words")
                    .limit(120)
                    .get()
                    .await()

            val wordsList =
                snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null

                    val id = (data["id"] as? Long)?.toString() ?: data["id"] as? String ?: UUID.randomUUID().toString()
                    val english = data["english"] as? String ?: ""
                    val translation = data["translation"] as? String ?: ""
                    val isLearned = data["isLearned"] as? Boolean ?: false
                    val difficulty = data["difficulty"] as? String ?: ""

                    Word(id = id, english = english, translation = translation, isLearned = isLearned, difficulty = difficulty)
                }

            wordsFlow.value = wordsList.shuffled() // Kelime listesini karıştır
            Log.d("WordRepositoryImpl", "Fetched words: $wordsList")

            return wordsFlow
        } catch (e: Exception) {
            Log.e("WordRepositoryImpl", "Error fetching words from Firestore: ${e.message}")
            return wordsFlow
        }
    }

    suspend fun addWordToFirebase(word: Word) {
        try {
            firestore.collection("words").add(word).await()
        } catch (e: Exception) {
            Log.e("WordRepositoryImpl", "Error adding word to Firestore: ${e.message}")
        }
    }
}
