package com.ari_d.justeatit.data.entities

data class Favorite(
    val name : String = "",
    val images : List<String> = listOf(),
    val price : String = "",
    val description : String = "",
    val product_id : String = "",
    val seller_id : String = "",
    val seller : String = ""
)