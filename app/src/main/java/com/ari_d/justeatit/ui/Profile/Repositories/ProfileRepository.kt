package com.ari_d.justeatit.ui.Profile.Repositories

import android.widget.TextView
import com.ari_d.justeatit.data.entities.Wallet
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun setNameandEmail(welcome: String, name: TextView, exclam: String, email: TextView) : Unit

    suspend fun UpdateUserNameandEmail(name: String) : Unit

    suspend fun LogOut() : Unit

    suspend fun insertWallet(wallet: Wallet)

    suspend fun deleteWallet(wallet: Wallet)

    suspend fun getWallet(id: Int): Wallet?

    fun getWallets(): Flow<List<Wallet>>
}