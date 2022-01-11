package com.ari_d.justeatit.ui.Details.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ari_d.justeatit.data.entities.Product
import com.ari_d.justeatit.other.Event
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.ui.Details.Repositories.DetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: DetailsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _getProductDetailsStatus = MutableLiveData<Event<Resource<Product>>>()
    val getProductDetailsStatus : LiveData<Event<Resource<Product>>> = _getProductDetailsStatus

    private val _addToShoppingBagStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val addToShoppingBagStatus : LiveData<Event<Resource<Boolean>>> = _addToShoppingBagStatus

    private val _getNumberOfCartItemsStatus = MutableLiveData<Event<Resource<Int>>>()
    val getNumberOfCartItemsStatus : LiveData<Event<Resource<Int>>> = _getNumberOfCartItemsStatus

    private val _deleteItemsFromCartStatus = MutableLiveData<Event<Resource<Void>>>()
    val deleteItemsFromCartStatus : LiveData<Event<Resource<Void>>> = _deleteItemsFromCartStatus

    private val _addToFavoritesStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val addToFavoritesStatus : LiveData<Event<Resource<Boolean>>> = _addToFavoritesStatus

    private val _deleteItemsFromFavoritesStatus = MutableLiveData<Event<Resource<Void>>>()
    val deleteItemsFromFavoritesStatus : LiveData<Event<Resource<Void>>> = _deleteItemsFromFavoritesStatus

    private val _getCartProductDetailsStatus = MutableLiveData<Event<Resource<Int>>>()
    val getCartProductDetailsStatus : LiveData<Event<Resource<Int>>> = _getCartProductDetailsStatus

    private val _setUiInterfaceStatus = MutableLiveData<Event<Resource<Product>>>()
    val setUiInterfaceStatus : LiveData<Event<Resource<Product>>> = _setUiInterfaceStatus

    private val _increaseCartNumberStatus = MutableLiveData<Event<Resource<Int>>>()
    val increaseCartNumberStatus : LiveData<Event<Resource<Int>>> = _increaseCartNumberStatus

    private val _decreaseCartNumberStatus = MutableLiveData<Event<Resource<Int>>>()
    val decreaseCartNumberStatus : LiveData<Event<Resource<Int>>> = _decreaseCartNumberStatus

    fun getProductDetails(product_id: String) {
        _getProductDetailsStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getProductDetails(product_id)
            _getProductDetailsStatus.postValue(Event(result))
        }
    }

    fun addToShoppingBag(product_id: String) {
        _addToShoppingBagStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.addToShoppingBag(product_id)
            _addToShoppingBagStatus.postValue(Event(result))
        }
    }

    fun addToFavorites(product_id: String) {
        _addToFavoritesStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.addToFavorites(product_id)
            _addToFavoritesStatus.postValue(Event(result))
        }
    }

    fun getNumberOfCartItems() {
        _getNumberOfCartItemsStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getNumberOfCartItems()
            _getNumberOfCartItemsStatus.postValue(Event(result))
        }
    }

    fun deleteItemFromCart(product_id: String) {
        _deleteItemsFromCartStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.deleteItemFromCart(product_id)
            _deleteItemsFromCartStatus.postValue(Event(result))
        }
    }

    fun deleteItemFromFavorites(product_id: String) {
        _deleteItemsFromFavoritesStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.deleteItemFromFavorites(product_id)
            _deleteItemsFromFavoritesStatus.postValue(Event(result))
        }
    }

    fun getCartProductDetails(product_id: String) {
        _getCartProductDetailsStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getCartProductDetails(product_id)
            _getCartProductDetailsStatus.postValue(Event(result))
        }
    }

    fun setUiInterface(product_id: String) {
        _setUiInterfaceStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.setUiInterface(product_id)
            _setUiInterfaceStatus.postValue(Event(result))
        }
    }

    fun increaseCartNo(value: String, product_id: String) {
        _increaseCartNumberStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.increaseCartNo(value,product_id)
            _increaseCartNumberStatus.postValue(Event(result))
        }
    }

    fun decreaseCartNo(value: String, product_id: String) {
        _decreaseCartNumberStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.DecreaseCartNo(value, product_id)
            _decreaseCartNumberStatus.postValue(Event(result))
        }
    }
}