package com.ari_d.justeatit.data.pagingsource

import androidx.paging.PagingSource
import com.ari_d.justeatit.data.entities.Orders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class TrackOrdersPagingSource(
    private val db: FirebaseFirestore,
    private val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
) : PagingSource<QuerySnapshot, Orders>(){

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Orders> {
        return try {
            val curPage = params.key ?: db
                .collection("users")
                .document(currentUser!!.uid)
                .collection("my orders")
                .whereNotEqualTo("status", "Delivered")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val lastDocumentSnapShot = curPage.documents[curPage.size() - 1]

            val nextPage = db
                .collection("users")
                .document(currentUser!!.uid)
                .collection("my orders")
                .whereNotEqualTo("status", "Delivered")
                .orderBy("date", Query.Direction.DESCENDING)
                .startAfter(lastDocumentSnapShot)
                .get()
                .await()

            LoadResult.Page(
                curPage.toObjects(Orders::class.java),
            null,
                nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}