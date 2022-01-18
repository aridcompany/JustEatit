package com.ari_d.justeatit.data.entities

import com.google.firebase.firestore.Exclude

data class Product(
    val name : String = "",
    val images : List<String> = listOf(),
    val price : String = "",
    val description : String = "",
    val product_id : String = "",
    val seller_id : String = "",
    val seller : String = "",
    val sizes : List<String> = listOf(),
    val increment_price : String = "",
    val shipping_fee : String = "",
    val contact_no : String = "",
    val quantity : String = "1",
    var favoritesList: List<String> = listOf(),
    var shoppingBagList: List<String> = listOf(),
    var isAvailable: Boolean = true,
    var stock: String = "",
    @get: Exclude var isAddedToFavorites: Boolean = false,
    @get: Exclude var isAddingToFavorites: Boolean = false,
    @get: Exclude var isAddedToShoppingBag: Boolean = false,
    @get: Exclude var isAddingToShoppingBag: Boolean = false
)