package com.ari_d.justeatit.data.entities

import com.google.firebase.auth.FirebaseAuth

data class Feedback(
    val rating: String = "",
    val info: String = "",
    val date: Long = System.currentTimeMillis(),
    val userEmail: String = FirebaseAuth.getInstance().currentUser!!.email.toString()
)