package com.erendogan6.translateify.di.module

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.erendogan6.translateify.BuildConfig
import com.erendogan6.translateify.data.remote.GeminiService
import com.erendogan6.translateify.data.remote.PexelsService
import com.erendogan6.translateify.utils.ResourcesProvider
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
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext
        context: Context,
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
    @Singleton
    fun provideGeminiService(resourcesProvider: ResourcesProvider): GeminiService = GeminiService(resourcesProvider)
}
