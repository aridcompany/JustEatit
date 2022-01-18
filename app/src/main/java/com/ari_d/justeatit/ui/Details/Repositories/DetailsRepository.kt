package com.ari_d.justeatit.ui.Details.Repositories

import com.ari_d.justeatit.data.entities.Product
import com.ari_d.justeatit.other.Resource

interface DetailsRepository {

    suspend fun getProductDetails(product_id: String) : Resource<Product>

    suspend fun addToShoppingBag(product_id: String) : Resource<Boolean>

    suspend fun addToFavorites(product_id: String) : Resource<Boolean>

    suspend fun getCartProductDetails(product_id: String) : Resource<Int>

    suspend fun setUiInterface(product_id: String) : Resource<Product>

    suspend fun increaseCartNo(value: String, product_id: String) : Resource<Int>

    suspend fun DecreaseCartNo(value: String, product_id: String) : Resource<Int>

}