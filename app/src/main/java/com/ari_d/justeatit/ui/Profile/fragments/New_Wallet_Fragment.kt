package com.ari_d.justeatit.ui.Profile.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import com.ari_d.justeatit.Extensions.*
import com.ari_d.justeatit.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_new_wallet.*

@AndroidEntryPoint
class New_Wallet_Fragment : Fragment(R.layout.fragment_new_wallet) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addCreditCardNumberTxtWatcher(requireContext(), img, et_card_number, TextInputLayout_cardNumber, '-')
        addCreditCardDateTxtWatcher(et_expiry, '/')
    }
}