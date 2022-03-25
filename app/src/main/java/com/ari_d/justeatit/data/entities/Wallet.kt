package com.ari_d.justeatit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Wallet (
    val cardNumber: String,
    val cvv: String,
    val expiryDate: String,
    val cardName: String,
    val cardType: String,
    @PrimaryKey val id: Int? = null
)