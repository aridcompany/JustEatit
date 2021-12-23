package com.ari_d.justeatit.ui.Main.Repositories

import com.ari_d.justeatit.data.entities.Favorite
import com.ari_d.justeatit.data.entities.Product
import com.ari_d.justeatit.other.Resource

interface MainRepository {

    suspend fun getProducts() : Resource<List<Product>>

    suspend fun getFavorites() : Resource<MutableList<Favorite>>

    suspend fun addToFavorites(product: Product) : Resource<Boolean>

    suspend fun addToShoppingBag(product: Product) : Resource<Boolean>

    suspend fun searchProduct(query: String) : Resource<List<Product>>

}