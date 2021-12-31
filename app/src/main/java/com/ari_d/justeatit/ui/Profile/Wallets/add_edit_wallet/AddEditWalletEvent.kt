package com.ari_d.justeatit.ui.Profile.Wallets.add_edit_wallet

sealed class AddEditWalletEvent {
    data class OnCardNameChanged(val cardName: String): AddEditWalletEvent()
    data class OnCardNumberChanged(val cardNumber: String): AddEditWalletEvent()
    data class OnCardCvvChanged(val cvv: String): AddEditWalletEvent()
    data class OnCardExpiryMonthChanged(val expiryMonth: String): AddEditWalletEvent()
    data class OnCardExpiryYearChanged(val expiryYear: String): AddEditWalletEvent()
    object OnSaveWalletClick: AddEditWalletEvent()
}
