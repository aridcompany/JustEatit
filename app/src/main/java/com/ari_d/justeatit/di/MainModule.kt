package com.ari_d.justeatit.di

import com.ari_d.justeatit.ui.Main.Repositories.DefaultMainRepository
import com.ari_d.justeatit.ui.Main.Repositories.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Singleton
    @Provides
    fun provideMainRepository() = DefaultMainRepository() as MainRepository
}