package com.ari_d.justeatit.data.pagingsource

import androidx.paging.PagingSource
import com.ari_d.justeatit.data.entities.Favorite
import com.ari_d.justeatit.data.entities.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class FavoritesPagingSource(
    private val db: FirebaseFirestore,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) : PagingSource<QuerySnapshot, Favorite>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Favorite> {
        return try {
            val curPage = params.key ?:  currentUser?.let {
                db.collection("users")
                    .document(it.uid)
                    .collection("favorites")
                    .get()
                    .await()
            }


            val lastDocumentSnapShot = curPage!!.documents[curPage.size() - 1]

            val nextPage = currentUser?.let {
                db.collection("users")
                    .document(it.uid)
                    .collection("favorites")
                    .startAfter(lastDocumentSnapShot)
                    .get()
                    .await()
            }

            LoadResult.Page(
                curPage.toObjects(Favorite::class.java),
                null,
                nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}