package com.erendogan6.translateify.di

import com.erendogan6.translateify.data.repository.LoginRepositoryImpl
import com.erendogan6.translateify.data.repository.RegisterRepositoryImpl
import com.erendogan6.translateify.domain.repository.LoginRepository
import com.erendogan6.translateify.domain.repository.RegisterRepository
import com.erendogan6.translateify.domain.usecase.login.LoginUseCase
import com.erendogan6.translateify.domain.usecase.register.RegisterUserUseCase
import com.erendogan6.translateify.domain.usecase.register.SaveUserToFirebaseUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserRepositoryModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideSaveUserToFirebaseUseCase(registerRepository: RegisterRepository): SaveUserToFirebaseUseCase =
        SaveUserToFirebaseUseCase(registerRepository)

    @Provides
    @Singleton
    fun provideregisterUserUseCase(registerRepository: RegisterRepository): RegisterUserUseCase = RegisterUserUseCase(registerRepository)

    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): RegisterRepository = RegisterRepositoryImpl(firebaseAuth, firestore)

    @Provides
    @Singleton
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): LoginRepository = LoginRepositoryImpl(firebaseAuth)

    @Provides
    @Singleton
    fun provideLoginUseCase(loginRepository: LoginRepository): LoginUseCase = LoginUseCase(loginRepository)
}
