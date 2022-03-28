package com.ari_d.justeatit.ui.Profile.Repositories

import android.content.Context
import com.ari_d.justeatit.data.entities.*
import com.ari_d.justeatit.other.Resource

interface ProfileRepository {

    suspend fun setNameandEmail() : Resource<User>

    suspend fun UpdateUserNameandEmail(name: String, profile_pic_uri: String) : Resource<String>

    suspend fun deleteProfilePhoto() : Resource<String>

    suspend fun LogOut() : Unit

    suspend fun insertWallet(wallet: Wallet) :  Unit

    suspend fun deleteWallet(wallet: Wallet) : Unit

    suspend fun getWallet(id: Int) : Wallet?

    suspend fun getAllWallets() : Resource<List<Wallet>>

    suspend fun getAddresses() : Resource<List<Address>>

    suspend fun makeAddressDefault(address: Address) : Resource<Boolean>

    suspend fun getDefaultAddress() : Resource<Boolean>

    suspend fun getSupportedLocations() : Resource<List<String>>

    suspend fun deleteAddress(address: Address) : Resource<Address>

    suspend fun createAddress(
        street_address: String,
        apt_suite: String,
        city: String,
        phone_number: String,
        additional_phoneNumber: String
    ): Resource<Address>

    suspend fun getHelpUrl() : Resource<Contact_Info>

    suspend fun getUrl() : Resource<Contact_Info>

    suspend fun createFeedback(rating: String, info: String) : Resource<String>

    suspend fun getUser(uid: String) : Resource<User>

    suspend fun checkShoppingBagForUnavailableProducts() : Resource<Boolean>

    suspend fun calculateTotal() : Resource<MutableList<Int>>

    suspend fun chargeCard(
        amountToPay: Int,
        cardNumber: Int,
        cardCVV: Int,
        cardExpiryMonth: Int,
        cardExpiryYear: Int,
        applicationContext: Context
    ) : Resource<Boolean>
}