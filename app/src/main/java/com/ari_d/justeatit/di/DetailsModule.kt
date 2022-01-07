package com.ari_d.justeatit.di

import com.ari_d.justeatit.ui.Details.Repositories.DefaultDetailsRepository
import com.ari_d.justeatit.ui.Details.Repositories.DetailsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DetailsModule {

    @Singleton
    @Provides
    fun provideAuthRepository() = DefaultDetailsRepository() as DetailsRepository
}