package com.erendogan6.translateify.di.module

import android.content.Context
import com.erendogan6.translateify.utils.ResourcesProvider
import com.erendogan6.translateify.utils.StringProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ResourcesModule {
    @Singleton
    @Provides
    fun provideResourcesProvider(
        @ApplicationContext
        context: Context,
    ): ResourcesProvider = StringProvider(context)
}
