package com.ari_d.justeatit.ui.Details.Repositories

import com.ari_d.justeatit.data.entities.Comment
import com.ari_d.justeatit.data.entities.Product
import com.ari_d.justeatit.data.entities.User
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.other.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class DefaultDetailsRepository : DetailsRepository {

    private val products = Firebase.firestore.collection("products")
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val users = Firebase.firestore.collection("users")
    private val firestore = Firebase.firestore
    private val comments = Firebase.firestore.collection("comments")

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
            if (product!!.stock.toInt() == 0) {
                products.document(product_id)
                    .update(
                        "available",
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
                if (product!!.stock.toInt() < value.toInt()) {
                    Resource.Success(1)
                } else {
                    currentUser?.let {
                        users.document(it.uid)
                            .collection("shopping bag")
                            .document(product_id)
                            .update(
                                "quantity",
                                value
                            )
                        result.clear()
                        result.add(value.toInt())
                    }
                    Resource.Success(result[0])
                }
            }
        }

    override suspend fun DecreaseCartNo(value: String, product_id: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val result = mutableListOf<Int>()
                if (value.toInt() > 0) {
                    currentUser?.let {
                        users.document(it.uid)
                            .collection("shopping bag")
                            .document(product_id)
                            .update(
                                "quantity",
                                value
                            )
                        result.clear()
                        result.add(value.toInt())
                    }
                    Resource.Success(result[0])
                } else {
                    currentUser?.let {
                        val cartItems = users.document(it.uid).collection("shopping bag")
                        firestore.runTransaction { transaction ->
                            val _productResult =
                                transaction.get(products.document(product_id))
                            val currentShoppingBag =
                                _productResult.toObject<Product>()?.shoppingBagList ?: listOf()
                            val cartResult = transaction.get(
                                cartItems
                                    .document(product_id)
                            )
                            if (cartResult.exists()) {
                                transaction.delete(cartItems.document(product_id))
                                transaction.update(
                                    products.document(product_id),
                                    "shoppingBagList",
                                    currentShoppingBag - currentUser.uid
                                )
                            }
                        }.await()
                        result.clear()
                        result.add(0)
                    }
                }
                Resource.Success(result[0])
            }
        }

    override suspend fun createComment(commentText: String, productId: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val uid = currentUser!!.uid
                val commentId = UUID.randomUUID().toString()
                val user = getUser(uid).data!!
                val comment = Comment(
                    commentId,
                    productId,
                    uid,
                    user.name,
                    commentText
                )
                comments.document(commentId).set(comment).await()
                Resource.Success(comment)
            }
        }

    override suspend fun deleteComment(comment: Comment) =
        withContext(Dispatchers.IO) {
            safeCall {
                comments.document(comment.commentId).delete().await()
                Resource.Success(comment)
            }
        }

    override suspend fun getCommentForProduct(productId: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val commentsForProduct = comments
                    .whereEqualTo("productId", productId)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .toObjects(Comment::class.java)
                    .onEach { comment ->
                        val user = getUser(comment.uid).data!!
                        comment.name = user.name
                    }
                Resource.Success(commentsForProduct)
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
}