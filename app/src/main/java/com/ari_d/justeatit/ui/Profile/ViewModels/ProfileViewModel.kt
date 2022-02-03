package com.ari_d.justeatit.ui.Profile.ViewModels

import android.content.Context
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Address
import com.ari_d.justeatit.data.entities.Wallet
import com.ari_d.justeatit.other.Event
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.other.walletEvent
import com.ari_d.justeatit.ui.Profile.Repositories.ProfileRepository
import com.ari_d.justeatit.util.Routes
import com.ari_d.justeatit.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    val wallets = repository.getWallets()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _setNameStatus = MutableLiveData<Event<Resource<Unit>>>()
    val setNameStatus: LiveData<Event<Resource<Unit>>> = _setNameStatus

    private val _updateUserDetailsStatus = MutableLiveData<Event<Resource<Unit>>>()
    val updateUserDetailsStautus: LiveData<Event<Resource<Unit>>> = _updateUserDetailsStatus

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

    private var deletedWallet: Wallet? = null

    fun onEvent(event: walletEvent) {
        when(event) {
            is walletEvent.onWalletClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_WALLET + "?walletId=${event.wallet.id}"))
            }
            is walletEvent.onAddWalletClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_WALLET))
            }
            is walletEvent.onUndoDeleteClick -> {
                deletedWallet?.let { wallet ->
                    viewModelScope.launch {
                        repository.insertWallet(wallet)
                    }
                }
            }
            is walletEvent.onDeleteWalletClick -> {
                viewModelScope.launch {
                    deletedWallet = event.wallet
                    repository.deleteWallet(event.wallet)
                    sendUiEvent(UiEvent.ShowSnackbar(
                        message = "Wallet deleted",
                        action = "Undo"
                    ))
                }
            }
            is walletEvent.onDoneChange -> {
                viewModelScope.launch {
                    repository.insertWallet(
                        event.wallet.copy(
                            isDone = event.isDone
                        )
                    )
                }
            }
        }
    }

    fun setNameandEmail(welcome: String, name: TextView, exclam: String, email: TextView) {
        _setNameStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.setNameandEmail(welcome, name, exclam, email)
            _setNameStatus.postValue(Event(Resource.Success(result)))
        }
    }

    fun updateUserDetails(name: String) {
        val error = if (name.isEmpty()) {
            applicationContext.getString(R.string.title_empty_input)
        } else null

        error?.let {
            _updateUserDetailsStatus.postValue(Event(Resource.Error(it)))
            return
        }
        _updateUserDetailsStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.UpdateUserNameandEmail(name)
            _updateUserDetailsStatus.postValue(Event(Resource.Success(result)))
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

    fun deleteAddress(address: Address) {
        _deleteAddressStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.deleteAddress(address)
            _deleteAddressStatus.postValue(Event(result))
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}