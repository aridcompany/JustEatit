package com.ari_d.justeatit.ui.Profile.Repositories

import android.app.Activity
import androidx.core.net.toUri
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.ari_d.justeatit.BuildConfig
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.*
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.other.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class DefaultProfileRepository(
    private val dao: WalletDao
) : ProfileRepository {

    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val users = Firebase.firestore.collection("users")
    private val sellers = Firebase.firestore.collection("sellers")
    private val storageRef = Firebase.storage.reference
    private val products = Firebase.firestore.collection("products")
    private val firestore = Firebase.firestore

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

    override suspend fun getAllWallets() = withContext(Dispatchers.IO) {
        safeCall {
            Resource.Success(dao.getAllWallets())
        }
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

    override suspend fun getOrders() = withContext(Dispatchers.IO) {
        safeCall {
            val orders = users
                .document(currentUser!!.uid)
                .collection("my orders")
                .whereNotEqualTo("status", "Delivered")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Orders::class.java)
            Resource.Success(orders)
        }
    }

    override suspend fun makeAddressDefault(address: Address) = withContext(Dispatchers.IO) {
        safeCall {
            var isUpdated = false
            val list = mutableListOf<Address>()
            val list_of_addresses = users.document(currentUser!!.uid)
                .collection("addresses")
                .whereEqualTo("default", true)
                .limit(1)
                .get()
                .await()
                .toObjects(Address::class.java)
            list.clear()
            list.addAll(list_of_addresses)
            if (list.isNotEmpty()) {
                for (document in list) {
                    users.document(currentUser.uid)
                        .collection("addresses")
                        .document(document.addressUID)
                        .update(
                            "default",
                            false
                        ).await()
                }
            }
            val addresses =
                users.document(currentUser.uid)
                    .collection("addresses")
                    .document(address.addressUID)
                    .get()
                    .await()
            if (addresses.exists()) {
                users.document(currentUser.uid)
                    .collection("addresses")
                    .document(address.addressUID).update(
                        "default",
                        true
                    ).await()
                isUpdated = true
            }
            Resource.Success(isUpdated)
        }
    }

    override suspend fun getDefaultAddress() = withContext(Dispatchers.IO) {
        safeCall {
            var isGotten = false
            val list = mutableListOf<Address>()
            val list_of_addresses = users.document(currentUser!!.uid)
                .collection("addresses")
                .whereEqualTo("default", true)
                .limit(1)
                .get()
                .await()
                .toObjects(Address::class.java)
            if (list_of_addresses.isEmpty()) {
                val addresses = users.document(currentUser.uid)
                    .collection("addresses")
                    .get()
                    .await()
                    .toObjects(Address::class.java)
                list.clear()
                list.addAll(addresses)
                if (list.isNotEmpty()) {
                    users.document(currentUser.uid)
                        .collection("addresses")
                        .document(list[0].addressUID)
                        .update(
                            "default",
                            true
                        ).await()
                    isGotten = true
                }
            }
            Resource.Success(isGotten)
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

    override suspend fun clearCart() = withContext(Dispatchers.IO) {
        safeCall {
            var isCleared = false
            val cartItems = users.document(currentUser!!.uid)
                .collection("shopping bag")

            cartItems
                .get()
                .await()
                .toObjects(Product::class.java)
                .onEach { product ->
                    firestore.runTransaction { transaction ->
                        val _productResult = transaction.get(products.document(product.product_id))
                        val _currentShoppingBag =
                            _productResult.toObject<Product>()
                        val currentShoppingBag = _currentShoppingBag?.shoppingBagList ?: listOf()
                        val cartResult = transaction.get(
                            cartItems
                                .document(product.product_id)
                        )
                        if (cartResult.exists()) {
                            transaction.delete(cartItems.document(product.product_id))
                            transaction.update(
                                products.document(product.product_id),
                                "shoppingBagList",
                                currentShoppingBag - currentUser.uid
                            )
                            isCleared = true
                        }
                    }
                }
            Resource.Success(isCleared)
        }
    }

    override suspend fun checkShoppingBagForUnavailableProducts() =
        withContext(Dispatchers.IO) {
            safeCall {
                val availabity = mutableListOf<String>()
                val _shoppingBagItem = users
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
                        availabity.add("unavailable")
                    } else {
                        isAvailable = true
                        _shoppingBagItem.document(item.product_id)
                            .update(
                                "available",
                                true
                            )
                    }
                }
                if (availabity.contains("unavailable"))
                    isAvailable = false
                Resource.Success(isAvailable)
            }
        }

    override suspend fun calculateTotal() = withContext(Dispatchers.IO) {
        safeCall {
            val shoppingBagItem = users
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
                val price = item.price.toInt() * item.quantity.toInt()
                subtotalList.add(price)
            }
            val subtotal = subtotalList.sum()
            val shipping_fee = (percent!!.shipping_fee_percent.toDouble() / 100) * subtotal
            list_of_values.add(0, shipping_fee.toInt())

            list_of_values.add(1, subtotal)

            val total = shipping_fee + subtotal
            list_of_values.add(2, total.toInt())

            Resource.Success(list_of_values)
        }
    }

    override suspend fun chargeCard(
        amountToPay: Int,
        cardNumber: String,
        cardCVV: Int,
        cardExpiryMonth: Int,
        cardExpiryYear: Int,
        applicationContext: Activity
    ): Resource<String> {
        var transaction_reference = ""
        return suspendCoroutine { continuation ->
            PaystackSdk.initialize(applicationContext)
            PaystackSdk.setPublicKey(BuildConfig.PSTK_PUBLIC_KEY)

            val card =
                Card(cardNumber, cardExpiryMonth, cardExpiryYear, cardCVV.toString())
            val charge = Charge()
            charge.amount = amountToPay * 100
            charge.email = currentUser!!.email
            charge.card = card
            PaystackSdk.chargeCard(
                applicationContext,
                charge,
                object : Paystack.TransactionCallback {
                    override fun onSuccess(transaction: Transaction?) {
                        transaction_reference = transaction!!.reference
                        continuation.resume(Resource.Success(transaction_reference))
                    }

                    override fun beforeValidate(transaction: Transaction?) {}

                    override fun onError(error: Throwable?, transaction: Transaction?) {
                        continuation.resume(Resource.Error(applicationContext.getString(R.string.title_payment_unsuccessful)))
                    }
                })
        }
    }

    override suspend fun createOrders(transaction_reference: String) =
        withContext(Dispatchers.IO) {
            var isSuccessful: Boolean
            safeCall {
                val date = DateFormat.getDateInstance().format(Calendar.getInstance().time)
                val user = getUser(currentUser!!.uid).data
                users
                    .document(currentUser.uid)
                    .collection("shopping bag")
                    .get()
                    .await()
                    .toObjects(Product::class.java)
                    .onEach { product ->
                        val orderID = UUID.randomUUID().toString()
                        users.document(currentUser.uid)
                            .collection("my orders")
                            .document(orderID)
                            .set(
                                Orders(
                                    image = product.images[0],
                                    price = product.price,
                                    name = product.name,
                                    status = "Pending",
                                    orderID = orderID,
                                    productID = product.product_id,
                                    transaction_reference = transaction_reference
                                )
                            )
                            .await()
                        sellers.document(product.seller_id)
                            .collection("user's orders")
                            .document("orders")
                            .collection(date)
                            .document(user!!.name)
                            .collection("orders")
                            .document(orderID)
                            .set(
                                Orders(
                                    image = product.images[0],
                                    price = product.price,
                                    name = product.name,
                                    status = "Pending",
                                    orderID = orderID,
                                    productID = product.product_id,
                                    transaction_reference = transaction_reference
                                )
                            )
                            .await()
                    }
                isSuccessful = true
                Resource.Success(isSuccessful)
            }
        }
}