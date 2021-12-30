package com.ari_d.justeatit.di

import android.app.Application
import androidx.room.Room
import com.ari_d.justeatit.data.entities.WalletDatabase
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

    @Provides
    @Singleton
    fun provideWalletDatabase(app: Application): WalletDatabase {
        return Room.databaseBuilder(
            app,
            WalletDatabase::class.java,
            "wallet_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideProfileRepository(db: WalletDatabase) : ProfileRepository {
        return DefaultProfileRepository(db.dao)
    }
}