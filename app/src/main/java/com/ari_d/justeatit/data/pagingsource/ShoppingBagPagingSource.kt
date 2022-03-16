package com.ari_d.justeatit.data.pagingsource

import androidx.paging.PagingSource
import com.ari_d.justeatit.data.entities.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await

class ShoppingBagPagingSource(
    private val db: FirebaseFirestore,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser,
) : PagingSource<QuerySnapshot, Product>(){

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Product> {
        return try {
            val shoppingBagItem = db.collection("users")
                .document(currentUser!!.uid)
                .collection("shopping bag")

            val curPage = params.key ?: db
                .collection("users")
                .document(currentUser!!.uid)
                .collection("shopping bag")
                .get()
                .await()

            val lastDocumentSnapShot = curPage.documents[curPage.size() - 1]

            val nextPage = db
                .collection("users")
                .document(currentUser!!.uid)
                .collection("shopping bag")
                .startAfter(lastDocumentSnapShot)
                .get()
                .await()

            LoadResult.Page(
                curPage.toObjects<Product>().onEach { product ->
                    val _product = db.collection("products")
                        .whereEqualTo("product_id", product.product_id)
                        .limit(1)
                        .get().await().toObjects<Product>()
                    _product?.let {
                        if (!it[0].isAvailable) {
                            !product.isAvailable
                            shoppingBagItem.document(product.product_id)
                                .update(
                                    "available",
                                    false
                                )
                        } else if (it[0].isAvailable) {
                            product.isAvailable
                            shoppingBagItem.document(product.product_id)
                                .update(
                                    "available",
                                    true
                                )
                        }
                    }
                },
                null,
                nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}