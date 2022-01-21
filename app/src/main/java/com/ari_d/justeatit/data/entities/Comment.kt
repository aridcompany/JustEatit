package com.ari_d.justeatit.data.entities

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Comment(
    val commentId: String,
    val productId: String,
    val uid: String,
    @get: Exclude
    var name: String,
    val comment: String,
    val date: Long = System.currentTimeMillis()
)