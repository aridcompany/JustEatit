package com.ari_d.justeatit.data.entities

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: Wallet)

    @Delete
    suspend fun deleteWallet(wallet: Wallet)

    @Query("SELECT * FROM wallet WHERE id = :id")
    suspend fun getWallet(id: Int): Wallet?

    @Query("SELECT * FROM wallet")
    fun getWallets(): Flow<List<Wallet>>

    @Query("SELECT * FROM wallet")
    suspend fun getAllWallets(): List<Wallet>
}