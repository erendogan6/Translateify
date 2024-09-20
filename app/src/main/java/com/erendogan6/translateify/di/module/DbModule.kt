package com.erendogan6.translateify.di.module

import android.content.Context
import androidx.room.Room
import com.erendogan6.translateify.data.local.AppDatabase
import com.erendogan6.translateify.data.local.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DbModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext
        context: Context,
    ): AppDatabase =
        Room
            .databaseBuilder(context, AppDatabase::class.java, "translateify_db")
            .build()

    @Provides
    fun provideWordDao(database: AppDatabase): WordDao = database.wordDao()
}
