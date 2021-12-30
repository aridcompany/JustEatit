package com.ari_d.justeatit.other

import com.ari_d.justeatit.data.entities.Wallet

sealed class walletEvent {
    data class onDeleteWalletClick(val wallet: Wallet): walletEvent()
    data class onDoneChange(val wallet: Wallet, val isDone: Boolean): walletEvent()
    object onUndoDeleteClick: walletEvent()
    data class onWalletClick(val wallet: Wallet): walletEvent()
    object onAddWalletClick: walletEvent()
}