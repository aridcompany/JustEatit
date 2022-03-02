package com.ari_d.justeatit.ui.Main.Repositories

import com.ari_d.justeatit.data.entities.Favorite
import com.ari_d.justeatit.data.entities.Product
import com.ari_d.justeatit.other.Constants.SEARCH_QUERY_SIZE
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.other.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

@ActivityScoped
class DefaultMainRepository : MainRepository {

    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val firestore = Firebase.firestore
    private val users = firestore.collection("users")
    private val products = Firebase.firestore.collection("products")

    override suspend fun getProducts() =
        withContext(Dispatchers.IO) {
            safeCall {
                val result = products.get().await()
                val products = result.toObjects<Product>().onEach { product ->
                    currentUser?.let {
                        product.isAddedToShoppingBag = currentUser.uid in product.shoppingBagList
                        product.isAddedToFavorites = currentUser.uid in product.favoritesList
                    }
                }
                Resource.Success(products)
            }
        }

    override suspend fun getFavorites() =
        withContext(Dispatchers.IO) {
            safeCall {
                val fav_list = mutableListOf<Favorite>()
                currentUser?.let {
                    users.document(it.uid)
                        .collection("favorites")
                        .get().await().forEach {
                            val fav = it.toObject<Favorite>()
                            fav_list.add(fav)
                        }
                }
                Resource.Success(fav_list)
            }
        }

    override suspend fun addToFavorites(product: Product) =
        withContext(Dispatchers.IO) {
            safeCall {
                var isAddedToFavorites = false
                currentUser?.let {
                    val favorites = users.document(currentUser.uid).collection("favorites")
                    firestore.runTransaction { transaction ->
                        val _productResult = transaction.get(products.document(product.product_id))
                        val currentFavorites =
                            _productResult.toObject<Product>()?.favoritesList ?: listOf()
                        val productResult = transaction.get(
                            favorites
                                .document(product.product_id)
                        )
                        if (productResult.exists()) {
                            transaction.delete(favorites.document(product.product_id))
                            transaction.update(
                                products.document(product.product_id),
                                "favoritesList",
                                currentFavorites - currentUser.uid
                            )
                        } else {
                            transaction.set(
                                favorites.document(product.product_id),
                                product
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

    override suspend fun addToShoppingBag(product: Product) =
        withContext(Dispatchers.IO) {
            safeCall {
                var isAddedToShoppingBag = false
                currentUser?.let {
                    val cartItems = users.document(currentUser.uid).collection("shopping bag")

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
                        } else {
                            if (_currentShoppingBag!!.stock.toInt() != 0) {
                                transaction.set(
                                    cartItems.document(product.product_id),
                                    product
                                )
                                transaction.update(
                                    products.document(product.product_id),
                                    "shoppingBagList",
                                    currentShoppingBag + currentUser.uid
                                )
                                isAddedToShoppingBag = true
                            } else {}
                        }
                    }.await()
                }
                Resource.Success(isAddedToShoppingBag)
            }
        }

    override suspend fun searchProduct(query: String) = withContext(Dispatchers.IO) {
        safeCall {
            val productResults =
                products.whereGreaterThanOrEqualTo("name", query.uppercase(Locale.ROOT)).limit(
                    SEARCH_QUERY_SIZE.toLong()
                )
                    .get().await().toObjects<Product>()
            productResults?.let {
                Resource.Success(productResults)
            }
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
}