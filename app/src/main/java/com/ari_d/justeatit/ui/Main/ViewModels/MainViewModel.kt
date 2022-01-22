package com.ari_d.justeatit.ui.Main.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ari_d.justeatit.data.entities.Favorite
import com.ari_d.justeatit.data.entities.Product
import com.ari_d.justeatit.data.pagingsource.FavoritesPagingSource
import com.ari_d.justeatit.data.pagingsource.ProductsPagingSource
import com.ari_d.justeatit.other.Constants.PAGE_SIZE
import com.ari_d.justeatit.other.Event
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.ui.Main.Repositories.MainRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _getProducts = MutableLiveData<Event<Resource<List<Product>>>>()
    val getProducts: LiveData<Event<Resource<List<Product>>>> = _getProducts

    private val _addToFavorites = MutableLiveData<Event<Resource<Boolean>>>()
    val addToFavorites: LiveData<Event<Resource<Boolean>>> = _addToFavorites

    private val _addToShoppingBag = MutableLiveData<Event<Resource<Boolean>>>()
    val addToShoppingBag: LiveData<Event<Resource<Boolean>>> = _addToShoppingBag

    private val _getFavorites = MutableLiveData<Event<Resource<MutableList<Favorite>>>>()
    val getFavorites: LiveData<Event<Resource<MutableList<Favorite>>>> = _getFavorites

    private val _searchResults = MutableLiveData<Event<Resource<List<Product>>>>()
    val searchResults: LiveData<Event<Resource<List<Product>>>> = _searchResults

    private val _cartNo = MutableLiveData<Event<Resource<Int>>>()
    val cartNo: LiveData<Event<Resource<Int>>> = _cartNo

    fun getPagingFlow(): Flow<PagingData<Product>> {
        val pagingSource = ProductsPagingSource(
            FirebaseFirestore.getInstance(),
        )
        return Pager(PagingConfig(PAGE_SIZE)) {
            pagingSource
        }.flow.cachedIn(viewModelScope)
    }

    fun getPagingFlowForFavorites(): Flow<PagingData<Favorite>> {
        val pagingSource = FavoritesPagingSource(
            FirebaseFirestore.getInstance(),
        )
        return Pager(PagingConfig(PAGE_SIZE)) {
            pagingSource
        }.flow.cachedIn(viewModelScope)
    }

    fun getCartItemsNo() {
        _cartNo.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getNumberOfCartItems()
            _cartNo.postValue(Event(result))
        }
    }

    fun searchProducts(query: String) {
        if (query.isEmpty()) return

        _searchResults.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.searchProduct(query)
            _searchResults.postValue(Event(result))
        }
    }

    fun addToFavorites(product: Product) {
        _addToFavorites.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.addToFavorites(product)
            _addToFavorites.postValue(Event(result))
        }
    }

    fun addToShoppingBag(product: Product) {
        _addToShoppingBag.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.addToShoppingBag(product)
            _addToShoppingBag.postValue(Event(result))
        }
    }
}