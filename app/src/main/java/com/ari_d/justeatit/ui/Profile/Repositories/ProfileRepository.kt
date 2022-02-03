package com.ari_d.justeatit.ui.Profile.Repositories

import android.widget.TextView
import com.ari_d.justeatit.data.entities.Address
import com.ari_d.justeatit.data.entities.SupportedLocations
import com.ari_d.justeatit.data.entities.Wallet
import com.ari_d.justeatit.other.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun setNameandEmail(
        welcome: String,
        name: TextView,
        exclam: String,
        email: TextView
    ): Unit

    suspend fun UpdateUserNameandEmail(name: String): Unit

    suspend fun LogOut(): Unit

    suspend fun insertWallet(wallet: Wallet)

    suspend fun deleteWallet(wallet: Wallet)

    suspend fun getWallet(id: Int): Wallet?

    fun getWallets(): Flow<List<Wallet>>

    suspend fun getAddresses(): Resource<List<Address>>

    suspend fun getSupportedLocations(): Resource<List<String>>

    suspend fun deleteAddress(address: Address): Resource<Address>

    suspend fun createAddress(
        street_address: String,
        apt_suite: String,
        city: String,
        phone_number: String,
        additional_phoneNumber: String
    ): Resource<Address>

}