package com.ari_d.justeatit.ui.Profile.Repositories

import android.widget.TextView
import androidx.core.net.toUri
import com.ari_d.justeatit.data.entities.*
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.other.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
    val storageRef = Firebase.storage.reference

    override suspend fun setNameandEmail() = withContext(Dispatchers.IO) {
        safeCall {
            val user = currentUser?.let {
                getUser(it.uid).data!!
            }
            Resource.Success(user!!)
        }
    }

    override suspend fun UpdateUserNameandEmail(name: String, profile_pic_uri: String) =
        withContext(Dispatchers.IO) {
            var profile_pic_Uri = ""
            safeCall {
                currentUser?.let { currentUser ->
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    if (profile_pic_uri != "") {
                        val profile_pic = profile_pic_uri.toUri()
                        val profileRef = storageRef
                            .child("profile pictures/${currentUser.uid}")
                            .putFile(profile_pic).await()
                        val _profile_pic_Url =
                            profileRef?.metadata?.reference?.downloadUrl?.await().toString()
                        profile_pic_Uri = _profile_pic_Url
                    }
                    currentUser.updateProfile(profileUpdates)
                    val uid = currentUser.uid
                    val user = mutableMapOf<String, Any>()
                    user["name"] = name
                    if (profile_pic_Uri != "") {
                        user["profile_pic"] = profile_pic_Uri
                    }
                    users.document(uid).set(
                        user,
                        SetOptions.merge()
                    ).await()
                }
                val result = profile_pic_Uri
                Resource.Success(result)
            }
        }

    override suspend fun deleteProfilePhoto() = withContext(Dispatchers.IO) {
        safeCall {
            storageRef
                .child("profile pictures/${currentUser!!.uid}")
                .delete().await()
            val uid = currentUser.uid
            val user = mutableMapOf<String, Any>()
            user["profile_pic"] = ""
            users.document(uid).set(
                user,
                SetOptions.merge()
            ).await()
            Resource.Success("")
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

    override suspend fun getHelpUrl() = withContext(Dispatchers.IO) {
        safeCall {
            val url = Firebase.firestore
                .collection("Just Eat it")
                .document("contact info")
                .get()
                .await()
                .toObject(Contact_Info::class.java)
            Resource.Success(url!!)
        }
    }

    override suspend fun getUrl() = withContext(Dispatchers.IO) {
        safeCall {
            val url = Firebase.firestore
                .collection("Just Eat it")
                .document("contact info")
                .get()
                .await()
                .toObject(Contact_Info::class.java)
            Resource.Success(url!!)
        }
    }

    override suspend fun createFeedback(rating: String, info: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val feedback = Firebase.firestore
                    .collection("Just Eat it")
                    .document("user feedbacks")
                    .collection("Feedbacks")
                feedback.document(currentUser!!.email.toString())
                    .set(
                        Feedback(
                            rating,
                            info
                        )
                    ).await()
                Resource.Success("Thanks for sending your feedback!")
            }
        }

    override suspend fun getUser(uid: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val user = users.document(uid).get().await().toObject<User>()
                    ?: throw IllegalStateException()
                Resource.Success(user)
            }
        }

    override suspend fun checkShoppingBagForUnavailableProducts() = withContext(Dispatchers.IO) {
        safeCall {
            val _shoppingBagItem = Firebase.firestore.collection("users")
                .document(currentUser!!.uid)
                .collection("shopping bag")
            val shoppingBagItem = _shoppingBagItem.get()
                .await()
                .toObjects(Product::class.java)

            var isAvailable = false
            for (item in shoppingBagItem) {
                if (!item.isAvailable) {
                    _shoppingBagItem.document(item.product_id)
                        .update(
                            "available",
                            false
                        )
                    isAvailable = false
                } else {
                    isAvailable = true
                    _shoppingBagItem.document(item.product_id)
                        .update(
                            "available",
                            true
                        )
                }
            }
            Resource.Success(isAvailable)
        }
    }

    override suspend fun calculateTotal() = withContext(Dispatchers.IO) {
        safeCall {
            val shoppingBagItem = Firebase.firestore.collection("users")
                .document(currentUser!!.uid)
                .collection("shopping bag")
                .get()
                .await()
                .toObjects(Product::class.java)
            val list_of_values = mutableListOf<Int>()

            val percent = Firebase.firestore
                .collection("Just Eat it")
                .document("contact info")
                .get()
                .await()
                .toObject(Contact_Info::class.java)

            val subtotalList = mutableListOf<Int>()
            for (item in shoppingBagItem) {
                val price = item.price.toInt()
                subtotalList.add(price)
            }
            val subtotal = subtotalList.sum()
            val shipping_fee = (percent!!.shipping_fee_percent.toInt() / 100) * subtotal
            list_of_values.add(0, shipping_fee)

            list_of_values.add(1, subtotal)

            val total = shipping_fee + subtotal
            list_of_values.add(2, total)

            Resource.Success(list_of_values)
        }
    }
}