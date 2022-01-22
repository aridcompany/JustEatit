package com.ari_d.justeatit.data.pagingsource

import androidx.paging.PagingSource
import com.ari_d.justeatit.data.entities.Comment
import com.ari_d.justeatit.ui.Details.Repositories.DefaultDetailsRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class CommentsPagingSource(
    private val db: FirebaseFirestore,
    private val repository: DefaultDetailsRepository,
    private val productId: String
) : PagingSource<QuerySnapshot, Comment>(){

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Comment> {
        return try {
            val curPage = params.key ?: db
                .collection("comments")
                .whereEqualTo("productId", productId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val lastDocumentSnapShot = curPage.documents[curPage.size() - 1]

            val nextPage = db
                .collection("comments")
                .whereEqualTo("productId", productId)
                .orderBy("date", Query.Direction.DESCENDING)
                .startAfter(lastDocumentSnapShot)
                .get()
                .await()

            LoadResult.Page(
                curPage.toObjects(Comment::class.java)
                    .onEach { comment ->
                        val user = repository.getUser(comment.uid).data!!
                        comment.name = user.name
                    },
            null,
                nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}