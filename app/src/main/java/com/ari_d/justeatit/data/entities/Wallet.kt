package com.ari_d.justeatit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Wallet (
    val cardNumber: String,
    val cvv: String,
    val expiryMonth: String,
    val expiryYear: String,
    val cardName: String,
    val isDone: Boolean,
    @PrimaryKey val id: Int? = null
)