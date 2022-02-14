package com.ari_d.justeatit.ui.Profile.Repositories

import android.widget.TextView
import com.ari_d.justeatit.data.entities.Address
import com.ari_d.justeatit.data.entities.Contact_Info
import com.ari_d.justeatit.data.entities.Feedback
import com.ari_d.justeatit.data.entities.Wallet
import com.ari_d.justeatit.other.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun setNameandEmail(
        name: TextView,
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

    suspend fun getHelpUrl(): Resource<Contact_Info>

    suspend fun getUrl(): Resource<Contact_Info>

    suspend fun createFeedback(rating: String, info: String) : Resource<String>

}