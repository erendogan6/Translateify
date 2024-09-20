package com.erendogan6.translateify.di.module

import com.erendogan6.translateify.data.repository.LoginRepositoryImpl
import com.erendogan6.translateify.domain.repository.LoginRepository
import com.erendogan6.translateify.domain.usecase.login.LoginUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginRepositoryModule {
    @Provides
    @Singleton
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): LoginRepository = LoginRepositoryImpl(firebaseAuth)

    @Provides
    @Singleton
    fun provideLoginUseCase(loginRepository: LoginRepository): LoginUseCase = LoginUseCase(loginRepository)
}
