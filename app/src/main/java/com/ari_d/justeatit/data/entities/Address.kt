package com.ari_d.justeatit.data.entities

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Address(
    val street_address: String = "",
    val apt_suite: String = "",
    val city: String = "",
    val phone_number: String = "",
    val additional_phoneNumber: String = "",
    val addressUID: String = "",
    val uid: String = FirebaseAuth.getInstance().currentUser!!.uid
)