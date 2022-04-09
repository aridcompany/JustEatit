package com.ari_d.justeatit.ui.Profile.ViewModels

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.*
import com.ari_d.justeatit.data.pagingsource.OrdersPagingSource
import com.ari_d.justeatit.data.pagingsource.ShoppingBagPagingSource
import com.ari_d.justeatit.data.pagingsource.TrackOrdersPagingSource
import com.ari_d.justeatit.other.Constants
import com.ari_d.justeatit.other.Event
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.ui.Profile.Repositories.ProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    private val _setNameStatus = MutableLiveData<Event<Resource<User>>>()
    val setNameStatus: LiveData<Event<Resource<User>>> = _setNameStatus

    private val _updateUserDetailsStatus = MutableLiveData<Event<Resource<String>>>()
    val updateUserDetailsStautus: LiveData<Event<Resource<String>>> = _updateUserDetailsStatus

    private val _deleteProfilePicStatus = MutableLiveData<Event<Resource<String>>>()
    val deleteProfilePicStatus: LiveData<Event<Resource<String>>> = _deleteProfilePicStatus

    private val _logOutStatus = MutableLiveData<Event<Resource<Unit>>>()
    val logOutStatus: LiveData<Event<Resource<Unit>>> = _logOutStatus

    private val _getAddressesStatus = MutableLiveData<Event<Resource<List<Address>>>>()
    val getAddressesStatus: LiveData<Event<Resource<List<Address>>>> = _getAddressesStatus

    private val _getSupportedLocationStaus = MutableLiveData<Event<Resource<List<String>>>>()
    val getSupportedLocationStatus: LiveData<Event<Resource<List<String>>>> = _getSupportedLocationStaus

    private val _createAddressStatus = MutableLiveData<Event<Resource<Address>>>()
    val createAddressStatus: LiveData<Event<Resource<Address>>> = _createAddressStatus

    private val _deleteAddressStatus = MutableLiveData<Event<Resource<Address>>>()
    val deleteAddressStatus: LiveData<Event<Resource<Address>>> = _deleteAddressStatus

    private val _getHelpUrlStatus = MutableLiveData<Event<Resource<Contact_Info>>>()
    val getHelpUrlStatus: LiveData<Event<Resource<Contact_Info>>> = _getHelpUrlStatus

    private val _getUrlStatus = MutableLiveData<Event<Resource<Contact_Info>>>()
    val getUrlStatus: LiveData<Event<Resource<Contact_Info>>> = _getUrlStatus

    private val _feedbackStatus = MutableLiveData<Event<Resource<String>>>()
    val feedbackStatus: LiveData<Event<Resource<String>>> = _feedbackStatus

    private val _checkShoppingBagForUnavailableProductsStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val checkShoppingBagForUnavailableProductsStatus: LiveData<Event<Resource<Boolean>>> = _checkShoppingBagForUnavailableProductsStatus

    private val _calculateTotalStatus = MutableLiveData<Event<Resource<MutableList<Int>>>>()
    val calculateTotalStatus: LiveData<Event<Resource<MutableList<Int>>>> = _calculateTotalStatus

    private val _makeDefaultAddressStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val makeDefaultAddressStatus: LiveData<Event<Resource<Boolean>>> = _makeDefaultAddressStatus

    private val _getDefaultAddressStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val getDefaultAddressStatus: LiveData<Event<Resource<Boolean>>> = _getDefaultAddressStatus

    private val _getAllWalletsStatus = MutableLiveData<Event<Resource<List<Wallet>>>>()
    val getAllWalletsStatus: LiveData<Event<Resource<List<Wallet>>>> = _getAllWalletsStatus

    private val _insertWalletStatus = MutableLiveData<Event<Resource<Unit>>>()
    val insertWalletStatus: LiveData<Event<Resource<Unit>>> = _insertWalletStatus

    private val _deleteWalletStatus = MutableLiveData<Event<Resource<Unit>>>()
    val deleteWalletStatus: LiveData<Event<Resource<Unit>>> = _deleteWalletStatus

    private val _getUserStatus = MutableLiveData<Event<Resource<User>>>()
    val getUserStatus: LiveData<Event<Resource<User>>> = _getUserStatus

    private val _chargeCardStatus = MutableLiveData<Event<Resource<String>>>()
    val chargeCardStatus: LiveData<Event<Resource<String>>> = _chargeCardStatus

    private val _createOrderStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val createOrderStatus: LiveData<Event<Resource<Boolean>>> = _createOrderStatus

    fun setNameandEmail() {
        _setNameStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.setNameandEmail()
            _setNameStatus.postValue(Event(result))
        }
    }

    fun updateUserDetails(name: String, profile_pic: String) {
        val error = if (name.isEmpty()) {
            applicationContext.getString(R.string.title_empty_input)
        } else null

        error?.let {
            _updateUserDetailsStatus.postValue(Event(Resource.Error(it)))
            return
        }
        _updateUserDetailsStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.UpdateUserNameandEmail(name, profile_pic)
            _updateUserDetailsStatus.postValue(Event(result))
        }
    }

    fun deleteProfilePhoto() {
        _deleteProfilePicStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.deleteProfilePhoto()
            _deleteProfilePicStatus.postValue(Event(result))
        }
    }

    fun logOut() {
        _logOutStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.LogOut()
            _logOutStatus.postValue(Event(Resource.Success(result)))
        }
    }

    fun getSupportedLocations() {
        _getSupportedLocationStaus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.getSupportedLocations()
            _getSupportedLocationStaus.postValue(Event(result))
        }
    }

    fun getAddresses() {
        _getAddressesStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.getAddresses()
            _getAddressesStatus.postValue(Event(result))
        }
    }

    fun createAddress(
        street_address: String,
        apt_suite: String,
        city: String,
        phone_number: String,
        additional_phoneNumber: String,
    ) {
        _createAddressStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.createAddress(
                street_address,
                apt_suite,
                city,
                phone_number,
                additional_phoneNumber,
            )
            _createAddressStatus.postValue(Event(result))
        }
    }

    fun makeAddressDefault(address: Address) {
        _makeDefaultAddressStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.makeAddressDefault(address)
            _makeDefaultAddressStatus.postValue(Event(result))
        }
    }

    fun getDefaultAddress() {
        _getDefaultAddressStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.getDefaultAddress()
            _getDefaultAddressStatus.postValue(Event(result))
        }
    }

    fun deleteAddress(address: Address) {
        _deleteAddressStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.deleteAddress(address)
            _deleteAddressStatus.postValue(Event(result))
        }
    }

    fun getUser(uid: String) {
        _getUserStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.getUser(uid)
            _getUserStatus.postValue(Event(result))
        }
    }

    fun getHelpUrl() {
        _getHelpUrlStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.getHelpUrl()
            _getHelpUrlStatus.postValue(Event(result))
        }
    }

    fun getUrl() {
        _getUrlStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.getUrl()
            _getUrlStatus.postValue(Event(result))
        }
    }

    fun createFeedback(rating: String, info: String) {
        if(rating.isEmpty() || info.isEmpty()) {
            _feedbackStatus.postValue(Event(Resource.Error(applicationContext.getString(R.string.title_provide))))
        } else {
            _feedbackStatus.postValue(Event(Resource.Loading()))
            viewModelScope.launch {
                val result = repository.createFeedback(rating, info)
                _feedbackStatus.postValue(Event(result))
            }
        }
    }

    fun checkShoppingBagForUnavailableProducts() {
        _checkShoppingBagForUnavailableProductsStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.checkShoppingBagForUnavailableProducts()
            _checkShoppingBagForUnavailableProductsStatus.postValue(Event(result))
        }
    }

    fun calculateTotal() {
        _calculateTotalStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.calculateTotal()
            _calculateTotalStatus.postValue(Event(result))
        }
    }

    fun getAllWallets() {
        _getAllWalletsStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.getAllWallets()
            _getAllWalletsStatus.postValue(Event(result))
        }
    }

    fun insertWallet(wallet: Wallet) {
        val error = if (wallet.cardNumber.isEmpty()) {
            applicationContext.getString(R.string.title_empty_cardNumber)
        } else if (wallet.cardNumber.length < 10){
            applicationContext.getString(R.string.title_invalid_card_number)
        } else if (wallet.cardName.isEmpty()) {
            applicationContext.getString(R.string.title_empty_card_name)
        } else if (wallet.expiryDate.isEmpty()) {
            applicationContext.getString(R.string.title_empty_cardExpiry)
        } else if (wallet.expiryDate.length < 4) {
            applicationContext.getString(R.string.title_invalid_card_date)
        } else if (wallet.cvv.isEmpty()) {
            applicationContext.getString(R.string.title_empty_cardCVV)
        } else if (wallet.cvv.length < 3) {
            applicationContext.getString(R.string.title_invalid_cardCVV)
        } else null

        error?.let {
            _insertWalletStatus.postValue(Event(Resource.Error(it)))
            return
        }
        _insertWalletStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.insertWallet(wallet)
            _insertWalletStatus.postValue(Event(Resource.Success(result)))
        }
    }

    fun deleteWallet(wallet: Wallet) {
        _deleteWalletStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.deleteWallet(wallet)
            _deleteWalletStatus.postValue(Event(Resource.Success(result)))
        }
    }

    fun chargeCard(
        amountToPay: Int,
        cardNumber: String,
        cardCVV: Int,
        cardExpiryMonth: Int,
        cardExpiryYear: Int,
        applicationContext: Activity
    ) {
        val error = if (amountToPay.toString().isEmpty()) {
            applicationContext.getString(R.string.title_unknown_error_occurred)
        } else if (cardNumber.toString().isEmpty()) {
            applicationContext.getString(R.string.title_unknown_error_occurred)
        } else if (cardCVV.toString().isEmpty()) {
            applicationContext.getString(R.string.title_unknown_error_occurred)
        } else if (cardExpiryMonth.toString().isEmpty()) {
            applicationContext.getString(R.string.title_unknown_error_occurred)
        } else if (cardExpiryYear.toString().isEmpty()) {
            applicationContext.getString(R.string.title_unknown_error_occurred)
        } else null

        error?.let {
            _chargeCardStatus.postValue(Event(Resource.Error(it)))
            return
        }
        _chargeCardStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.chargeCard(amountToPay, cardNumber, cardCVV, cardExpiryMonth, cardExpiryYear, applicationContext)
            _chargeCardStatus.postValue(Event(result))
        }
    }

    fun createOrder(transaction_reference: String) {
        _createOrderStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.createOrders(transaction_reference)
            _createOrderStatus.postValue(Event(result))
        }
    }

    fun getPagingFlow(): Flow<PagingData<Orders>> {
        return Pager(PagingConfig(Constants.PAGE_SIZE)) {
            OrdersPagingSource(
                FirebaseFirestore.getInstance()
            )
        }.flow.cachedIn(viewModelScope)
    }

    fun getPagingFlowForTracking(): Flow<PagingData<Orders>> {
        return Pager(PagingConfig(Constants.PAGE_SIZE)) {
            TrackOrdersPagingSource(
                FirebaseFirestore.getInstance()
            )
        }.flow.cachedIn(viewModelScope)
    }

    fun getPagingFlowForShoppingBag(): Flow<PagingData<Product>> {
        return Pager(PagingConfig(Constants.PAGE_SIZE)) {
            ShoppingBagPagingSource(
                FirebaseFirestore.getInstance()
            )
        }.flow.cachedIn(viewModelScope)
    }
}