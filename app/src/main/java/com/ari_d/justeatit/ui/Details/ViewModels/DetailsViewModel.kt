package com.ari_d.justeatit.ui.Details.ViewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ari_d.justeatit.data.entities.Product
import com.ari_d.justeatit.other.Event
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.ui.Details.Repositories.DetailsRepository
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: DetailsRepository,
    private val applicationContext: Context,
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

    private val _getFavoritesProductDetailsStatus = MutableLiveData<Event<Resource<Int>>>()
    val getFavoritesProductDetailsStatus : LiveData<Event<Resource<Int>>> = _getFavoritesProductDetailsStatus

    private val _setUiInterfaceStatus = MutableLiveData<Event<Resource<Task<Void>>>>()
    val setUiInterfaceStatus : LiveData<Event<Resource<Task<Void>>>> = _setUiInterfaceStatus

    private val _increaseCartNumberStatus = MutableLiveData<Event<Resource<Int>>>()
    val increaseCartNumberStatus : LiveData<Event<Resource<Int>>> = _increaseCartNumberStatus

    private val _decreaseCartNumberStatus = MutableLiveData<Event<Resource<Int>>>()
    val decreaseCartNumberStatus : LiveData<Event<Resource<Int>>> = _decreaseCartNumberStatus
}