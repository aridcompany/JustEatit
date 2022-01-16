package com.ari_d.justeatit.ui.Details.Repositories

import com.ari_d.justeatit.data.entities.Product
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.other.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultDetailsRepository : DetailsRepository {

    private val products = Firebase.firestore.collection("products")
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val users = Firebase.firestore.collection("users")
    private val firestore = Firebase.firestore

    override suspend fun getProductDetails(product_id: String) = withContext(Dispatchers.IO) {
        safeCall {
            val _product = products.document(product_id).get().await()
            val product = _product.toObject<Product>()
            Resource.Success(product!!)
        }
    }

    override suspend fun addToShoppingBag(product_id: String) = withContext(Dispatchers.IO) {
        safeCall {
            var isAddedToShoppingBag = false
            currentUser?.let {
                val cartItems = users.document(currentUser.uid).collection("shopping bag")
                firestore.runTransaction { transaction ->
                    val _productResult = transaction.get(
                        products.document(product_id)
                    ).toObject<Product>()
                    val currentShoppingBag =
                        _productResult?.shoppingBagList ?: listOf()
                    transaction.set(
                        cartItems.document(product_id),
                        _productResult!!
                    )
                    transaction.update(
                        products.document(product_id),
                        "shoppingBagList",
                        currentShoppingBag + currentUser.uid
                    )
                    isAddedToShoppingBag = true
                }.await()
            }
            Resource.Success(isAddedToShoppingBag)
        }
    }

    override suspend fun addToFavorites(product_id: String) = withContext(Dispatchers.IO) {
        safeCall {
            var isAddedToFavorites = false
            val product = products.document(product_id)
                .get()
                .await()
                .toObject<Product>()
            currentUser?.let {
                val favorites = users.document(currentUser.uid).collection("favorites")
                firestore.runTransaction { transaction ->
                    val _productResult = transaction.get(products.document(product_id))
                    val currentFavorites =
                        _productResult.toObject<Product>()?.favoritesList ?: listOf()
                    val productResult = transaction.get(
                        favorites
                            .document(product_id)
                    )
                    if (productResult.exists()) {
                        transaction.delete(favorites.document(product_id))
                        transaction.update(
                            products.document(product_id),
                            "favoritesList",
                            currentFavorites - currentUser.uid
                        )
                    } else {
                        transaction.set(
                            favorites.document(product_id),
                            product!!
                        )
                        transaction.update(
                            products.document(product.product_id),
                            "favoritesList",
                            currentFavorites + currentUser.uid
                        )
                        isAddedToFavorites = true
                    }
                }.await()
            }
            Resource.Success(isAddedToFavorites)
        }
    }

    override suspend fun getNumberOfCartItems() = withContext(Dispatchers.IO) {
        safeCall {
            val _list = mutableListOf<Product>()
            currentUser?.let {
                users.document(it.uid).collection("shopping bag").get().await().forEach {
                    val item = it.toObject<Product>()
                    _list.add(item)
                }
            }
            val cartNo = _list.size
            Resource.Success(cartNo)
        }
    }

    override suspend fun deleteItemFromCart(product_id: String) = withContext(Dispatchers.IO) {
        safeCall {
            val result = currentUser?.let {
                users.document(it.uid)
                    .collection("shopping bag")
                    .document(product_id)
                    .delete()
                    .await()
            }
            Resource.Success(result!!)
        }
    }

    override suspend fun deleteItemFromFavorites(product_id: String) = withContext(Dispatchers.IO) {
        safeCall {
            val result = currentUser?.let {
                users.document(it.uid)
                    .collection("favorites")
                    .document(product_id)
                    .delete()
                    .await()
            }
            Resource.Success(result!!)
        }
    }

    override suspend fun getCartProductDetails(product_id: String) = withContext(Dispatchers.IO) {
        safeCall {
            val result = mutableListOf<Int>()
            currentUser?.let {
                val _product = users.document(it.uid)
                    .collection("shopping bag")
                    .document(product_id)
                    .get()
                    .await()
                _product?.let {
                    val product = it.toObject<Product>()
                    val _result = product!!.quantity.toInt()
                    result.clear()
                    result.add(_result)
                }
            }
            Resource.Success(result[0])
        }
    }

    override suspend fun setUiInterface(product_id: String) = withContext(Dispatchers.IO) {
        safeCall {
            val product = products.document(product_id)
                .get()
                .await()
                .toObject<Product>()
            if (product!!.stock.equals(0)) {
                products.document(product_id)
                    .update(
                        "isAvailable",
                        false
                    )
            }
            Resource.Success(product)
        }
    }

    override suspend fun increaseCartNo(value: String, product_id: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val product = products.document(product_id).get().await().toObject<Product>()
                val result = mutableListOf<Int>()
                if (product!!.stock < value) {
                    Resource.Success(value.toInt())
                } else {
                    currentUser?.let {
                        val quantity = users.document(it.uid)
                            .collection("shopping bag")
                            .document(product_id)
                            .get()
                            .await()
                            .toObject<Product>()
                            ?.quantity!!.toInt()
                        users.document(it.uid)
                            .collection("shopping bag")
                            .document(product_id)
                            .update(
                                "quantity",
                                quantity + 1
                            )
                        result.clear()
                        result.add(quantity + 1)
                    }
                    Resource.Success(result[0])
                }
            }
        }

    override suspend fun DecreaseCartNo(value: String, product_id: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val result = mutableListOf<Int>()
                if (value < "1") {
                    currentUser?.let {
                        val quantity = users.document(it.uid)
                            .collection("shopping bag")
                            .document(product_id)
                            .get()
                            .await()
                            .toObject<Product>()
                            ?.quantity!!.toInt()
                        users.document(it.uid)
                            .collection("shopping bag")
                            .document(product_id)
                            .update(
                                "quantity",
                                quantity - 1
                            )
                        result.clear()
                        result.add(quantity - 1)
                    }
                    Resource.Success(result[0])
                } else {
                    currentUser?.let {
                        users.document(it.uid)
                            .collection("shopping bag")
                            .document(product_id)
                            .delete()
                            .await()
                        result.clear()
                        result.add(0)
                    }
                }
                Resource.Success(result[0])
            }
        }
}