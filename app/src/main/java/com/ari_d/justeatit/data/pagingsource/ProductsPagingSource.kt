package com.ari_d.justeatit.data.pagingsource

import androidx.paging.PagingSource
import com.ari_d.justeatit.data.entities.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await

class ProductsPagingSource(
    private val db: FirebaseFirestore,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) : PagingSource<QuerySnapshot, Product>(){

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Product> {
        return try {
            val curPage = params.key ?: db
                .collection("products")
                .get()
                .await()

            val lastDocumentSnapShot = curPage.documents[curPage.size() - 1]

            val nextPage = db
                .collection("products")
                .startAfter(lastDocumentSnapShot)
                .get()
                .await()

            LoadResult.Page(
                curPage.toObjects<Product>().onEach { product ->
                    currentUser?.let {
                        product.isAddedToShoppingBag = currentUser.uid in product.shoppingBagList
                        product.isAddedToFavorites = currentUser.uid in product.favoritesList
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