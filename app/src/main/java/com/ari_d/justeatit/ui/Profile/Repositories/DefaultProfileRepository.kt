package com.ari_d.justeatit.ui.Profile.Repositories

import android.widget.TextView
import com.ari_d.justeatit.data.entities.*
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.other.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class DefaultProfileRepository(
    private val dao: WalletDao
) : ProfileRepository {

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val users = Firebase.firestore.collection("users")

    override suspend fun setNameandEmail(
        welcome: String,
        name: TextView,
        exclam: String,
        email: TextView
    ) {
        return withContext(Dispatchers.IO) {
            safeCall {
                val user_email = currentUser?.email.toString()
                val user_name = currentUser?.displayName.toString()
                val result = currentUser?.let {
                    if (user_name == "null") {
                        name.text = welcome + exclam
                    } else {
                        name.text = welcome + " " + user_name + exclam
                    }
                    email.text = user_email
                }
                Resource.Success(result)
            }
        }
    }

    override suspend fun UpdateUserNameandEmail(name: String) {
        return withContext(Dispatchers.IO) {
            safeCall {
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }

                val result = currentUser?.let {
                    currentUser.updateProfile(profileUpdates)
                    val uid = currentUser.uid
                    val user = User(uid, name)
                    users.document(uid).set(user).await()
                }
                Resource.Success(result)
            }
        }
    }

    override suspend fun LogOut() {
        withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.signOut()
                Resource.Success(result)
            }
        }
    }

    override suspend fun insertWallet(wallet: Wallet) {
        dao.insertWallet(wallet)
    }

    override suspend fun deleteWallet(wallet: Wallet) {
        dao.deleteWallet(wallet)
    }

    override suspend fun getWallet(id: Int): Wallet? {
        return dao.getWallet(id)
    }

    override fun getWallets(): Flow<List<Wallet>> {
        return dao.getWallets()
    }

    override suspend fun getAddresses() = withContext(Dispatchers.IO) {
        safeCall {
            val addresses =
                users.document(currentUser!!.uid)
                    .collection("addresses")
                    .get()
                    .await()
                    .toObjects(Address::class.java)
            Resource.Success(addresses)
        }
    }

    override suspend fun getSupportedLocations() = withContext(Dispatchers.IO) {
        safeCall {
            val supported_locations =
                Firebase.firestore
                    .collection("Just Eat it")
                    .document("supported locations")
                    .get()
                    .await()
                    .toObject(SupportedLocations::class.java)
            Resource.Success(supported_locations!!.supportedLocations)
        }
    }

    override suspend fun deleteAddress(address: Address) = withContext(Dispatchers.IO) {
        users.document(currentUser!!.uid)
            .collection("addresses")
            .document(address.addressUID)
            .delete()
            .await()
        Resource.Success(address)
    }

    override suspend fun createAddress(
        street_address: String,
        apt_suite: String,
        city: String,
        phone_number: String,
        additional_phoneNumber: String
    ) = withContext(Dispatchers.IO) {
        safeCall {
            val addressID = street_address.lowercase(Locale.getDefault())
            val address = Address(
                street_address,
                apt_suite,
                city,
                phone_number,
                additional_phoneNumber,
                addressID
            )
            users.document(currentUser!!.uid)
                .collection("addresses")
                .document(addressID)
                .set(address)
                .await()
            Resource.Success(address)
        }
    }
}