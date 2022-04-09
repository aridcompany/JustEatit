package com.ari_d.justeatit.data.entities

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue

data class Orders(
    val Image: String = "",
    val Name: String = "",
    val price: String = "",
    val status: String = "",
    val date: Long = System.currentTimeMillis(),
    val timeStamp: String = FieldValue.serverTimestamp().toString(),
    val orderID: String = "",
    val productID: String = "",
    val userId: String = FirebaseAuth.getInstance().currentUser!!.uid,
    val transaction_reference: String = ""
)