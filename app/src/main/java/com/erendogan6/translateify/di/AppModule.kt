package com.erendogan6.translateify.di

import android.content.Context
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.erendogan6.translateify.BuildConfig
import com.erendogan6.translateify.data.local.AppDatabase
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
import com.erendogan6.translateify.utils.ResourcesProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
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
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
    ): OkHttpClient {
        val cacheSize = 50L * 1024 * 1024
        val cacheDirectory = File(context.cacheDir, "http-cache")
        val cache = Cache(cacheDirectory, cacheSize)

        val okHttpBuilder =
            OkHttpClient
                .Builder()
                .cache(cache)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val chuckerInterceptor =
                ChuckerInterceptor
                    .Builder(context)
                    .collector(ChuckerCollector(context))
                    .maxContentLength(250000L)
                    .alwaysReadResponseBody(true)
                    .build()

            okHttpBuilder.addInterceptor(chuckerInterceptor)
        }

        return okHttpBuilder.build()
    }

    @Provides
    @Singleton
    fun providePexelsService(okHttpClient: OkHttpClient): PexelsService =
        Retrofit
            .Builder()
            .baseUrl("https://api.pexels.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PexelsService::class.java)

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

    @Provides
    @Singleton
    fun provideGeminiService(resourcesProvider: ResourcesProvider): GeminiService = GeminiService(resourcesProvider)
}
