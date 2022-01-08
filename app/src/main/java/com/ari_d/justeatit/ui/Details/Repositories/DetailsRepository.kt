package com.ari_d.justeatit.ui.Details.Repositories

import com.ari_d.justeatit.data.entities.Product
import com.ari_d.justeatit.other.Resource
import com.google.android.gms.tasks.Task

interface DetailsRepository {

    suspend fun getProductDetails(product_id: String) : Resource<Product>

    suspend fun addToShoppingBag(product_id: String) : Resource<Boolean>

    suspend fun addToFavorites(product_id: String) : Resource<Boolean>

    suspend fun getNumberOfCartItems() : Resource<Int>

    suspend fun deleteItemFromCart(product_id: String) : Resource<Void>

    suspend fun deleteItemFromFavorites(product_id: String) : Resource<Void>

    suspend fun getCartProductDetails(product_id: String) : Resource<Int>

    suspend fun getFavoritesProductDetails(product_id: String) : Resource<Int>

    suspend fun setUiInterface(product_id: String) : Resource<Task<Void>>

    suspend fun increaseCartNo(value: String, product_id: String) : Resource<Int>

    suspend fun DecreaseCartNo(value: String, product_id: String) : Resource<Int>

}