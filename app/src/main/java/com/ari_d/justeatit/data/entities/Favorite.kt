package com.ari_d.justeatit.data.entities

data class Favorite(
    val name : String = "",
    val images : List<String> = listOf(),
    val price : String = "",
    val description : String = "",
    val product_id : String = "",
    val seller_id : String = "",
    val seller : String = "",
    val comments : List<String> = listOf(),
    val sizes : List<String> = listOf(),
    val increment_price : String = "",
)