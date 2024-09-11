package com.erendogan6.translateify.di

import android.content.Context
import androidx.room.Room
import com.erendogan6.translateify.data.local.AppDatabase
import com.erendogan6.translateify.data.local.WordDao
import com.erendogan6.translateify.data.repository.WordRepositoryImpl
import com.erendogan6.translateify.domain.repository.WordRepository
import com.erendogan6.translateify.domain.usecase.GetLearnedWordsUseCase
import com.erendogan6.translateify.domain.usecase.GetRandomWordsUseCase
import com.erendogan6.translateify.domain.usecase.UpdateLearnedStatusUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(context, AppDatabase::class.java, "translateify_db")
            .build()

    @Provides
    fun provideWordDao(database: AppDatabase): WordDao = database.wordDao()

    @Provides
    fun provideWordRepository(wordDao: WordDao): WordRepository = WordRepositoryImpl(wordDao)

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

}
