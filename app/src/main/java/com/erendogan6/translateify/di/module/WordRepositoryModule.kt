package com.erendogan6.translateify.di.module

import com.erendogan6.translateify.data.local.WordDao
import com.erendogan6.translateify.data.remote.GeminiService
import com.erendogan6.translateify.data.remote.PexelsService
import com.erendogan6.translateify.data.repository.WordRepositoryImpl
import com.erendogan6.translateify.domain.repository.WordRepository
import com.erendogan6.translateify.domain.usecase.AddWordUseCase
import com.erendogan6.translateify.domain.usecase.GetLearnedWordsUseCase
import com.erendogan6.translateify.domain.usecase.GetRandomWordsUseCase
import com.erendogan6.translateify.domain.usecase.LoadWordsUseCase
import com.erendogan6.translateify.domain.usecase.UpdateLearnedStatusUseCase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WordRepositoryModule {
    @Provides
    fun provideWordRepository(
        wordDao: WordDao,
        geminiService: GeminiService,
        pexelsService: PexelsService,
        firestore: FirebaseFirestore,
    ): WordRepository = WordRepositoryImpl(wordDao, geminiService, pexelsService, firestore)

    @Provides
    @Singleton
    fun provideGetRandomWordsUseCase(wordRepository: WordRepository): GetRandomWordsUseCase = GetRandomWordsUseCase(wordRepository)

    @Provides
    @Singleton
    fun provideGetLearnedWordsUseCase(wordRepository: WordRepository): GetLearnedWordsUseCase = GetLearnedWordsUseCase(wordRepository)

    @Provides
    @Singleton
    fun provideUpdateLearnedStatusUseCase(wordRepository: WordRepository): UpdateLearnedStatusUseCase =
        UpdateLearnedStatusUseCase(wordRepository)

    @Provides
    @Singleton
    fun provideAddWordUseCase(wordRepository: WordRepository): AddWordUseCase = AddWordUseCase(wordRepository)

    @Provides
    @Singleton
    fun provideLoadWordsUseCase(wordRepository: WordRepository): LoadWordsUseCase = LoadWordsUseCase(wordRepository)
}
