package com.ari_d.justeatit.ui.Profile.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ari_d.justeatit.Adapters.MyWalletAdapter
import com.ari_d.justeatit.Extensions.*
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Wallet
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_new_wallet.*
import javax.inject.Inject

@AndroidEntryPoint
class New_Wallet_Fragment : Fragment(R.layout.fragment_new_wallet) {

    val viewModel : ProfileViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addCreditCardNumberTxtWatcher(requireContext(), cardType, img, et_card_number, TextInputLayout_cardNumber, '-')
        addCreditCardDateTxtWatcher(et_expiry, '/')
        subsribeToObservers()
        btn_pay.setOnClickListener {
            viewModel.insertWallet(Wallet(
                cardName = et_card_name.text.toString(),
                cardNumber = et_card_number.text.toString().replace("-",""),
                expiryDate = et_expiry.text.toString().replace("/",""),
                cvv = et_cvv.text.toString(),
                cardType = cardType.text.toString()
            ))
        }
    }

    private fun subsribeToObservers() {
        viewModel.insertWalletStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { snackbar(it) },
            onLoading = {}
        ){
            findNavController().popBackStack()
        })
    }
}