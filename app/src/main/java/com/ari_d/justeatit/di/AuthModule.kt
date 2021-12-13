package com.ari_d.justeatit.di

import com.ari_d.justeatit.ui.Auth.Repositories.AuthReposirory
import com.ari_d.justeatit.ui.Auth.Repositories.DefaultAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideAuthRepository() = DefaultAuthRepository() as AuthReposirory
}