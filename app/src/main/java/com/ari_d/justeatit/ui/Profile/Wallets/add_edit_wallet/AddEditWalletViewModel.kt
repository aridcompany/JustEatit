package com.ari_d.justeatit.ui.Profile.Wallets.add_edit_wallet

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ari_d.justeatit.data.entities.Wallet
import com.ari_d.justeatit.ui.Profile.Repositories.ProfileRepository
import com.ari_d.justeatit.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditWalletViewModel @Inject constructor(
    private val repository: ProfileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var wallet by mutableStateOf<Wallet?>(null)
        private set

    var cardName by mutableStateOf("")
        private set

    var cardNumber by mutableStateOf("")
        private set

    var cvv by mutableStateOf("")
        private set

    var expiryDate by mutableStateOf("")
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val walletId = savedStateHandle.get<Int>("walletId")!!
        if (walletId != -1) {
            viewModelScope.launch {
                repository.getWallet(walletId)?.let { wallet ->
                    cardName = wallet.cardName
                    cardNumber = wallet.cardNumber
                    cvv = wallet.cvv
                    expiryDate = wallet.expiryDate
                    this@AddEditWalletViewModel.wallet = wallet
                }
            }
        }
    }

    fun onEvent(event: AddEditWalletEvent) {
        when (event) {
            is AddEditWalletEvent.OnCardNameChanged -> {
                cardName = event.cardName
            }
            is AddEditWalletEvent.OnCardNumberChanged -> {
                cardNumber = event.cardNumber
            }
            is AddEditWalletEvent.OnCardCvvChanged -> {
                cvv = event.cvv
            }
            is AddEditWalletEvent.OnCardExpiryDateChanged -> {
                expiryDate = event.expiryDate
            }
            is AddEditWalletEvent.OnSaveWalletClick -> {
                viewModelScope.launch {
                    if (cardName.isBlank() || cardNumber.isBlank() || cvv.isBlank() || expiryDate.isBlank()) {
                        sendUiEvent(
                            UiEvent.ShowSnackbar(
                                message = "No field should be left blank"
                            )
                        )
                        return@launch
                    } else if (cvv.length != 3) {
                        sendUiEvent(
                            UiEvent.ShowSnackbar(
                                message = "Invalid CVV!"
                            )
                        )
                        return@launch
                    } else if (expiryDate.length != 4) {
                        sendUiEvent(
                            UiEvent.ShowSnackbar(
                                message = "Invalid Expiration date/month!"
                            )
                        )
                        return@launch
                    } else if (cardNumber.length < 10) {
                        sendUiEvent(
                            UiEvent.ShowSnackbar(
                                message = "Invalid Card Number!"
                            )
                        )
                        return@launch
                    }
                    repository.insertWallet(
                        Wallet(
                            cardName = cardName,
                            cardNumber = cardNumber,
                            cvv = cvv,
                            expiryDate = expiryDate,
                            isDone = wallet?.isDone ?: false,
                            id = wallet?.id
                        )
                    )
                    sendUiEvent(UiEvent.popBackStack)
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}