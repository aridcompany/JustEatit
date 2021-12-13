package com.ari_d.justeatit.di

import com.ari_d.justeatit.ui.Profile.Repositories.DefaultProfileRepository
import com.ari_d.justeatit.ui.Profile.Repositories.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Singleton
    @Provides
    fun provideProfileRepository() = DefaultProfileRepository() as ProfileRepository
}