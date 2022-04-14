package com.ari_d.justeatit.data.entities

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
data class Orders(
    val image: String = "",
    val name: String = "",
    val price: String = "",
    val status: String = "",
    val date: Long = System.currentTimeMillis(),
    val timeStamp: String = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date()),
    val orderID: String = "",
    val productID: String = "",
    val userId: String = FirebaseAuth.getInstance().currentUser!!.uid,
    val transaction_reference: String = "",
    val transportation_status: String = ""
)