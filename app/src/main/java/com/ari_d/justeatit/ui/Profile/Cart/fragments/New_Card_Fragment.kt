package com.ari_d.justeatit.ui.Profile.Cart.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ari_d.justeatit.Extensions.addCreditCardDateTxtWatcher
import com.ari_d.justeatit.Extensions.addCreditCardNumberTxtWatcher
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Wallet
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_new_card.*
import java.text.DecimalFormat

@AndroidEntryPoint
class New_Card_Fragment : Fragment(R.layout.fragment_new_card) {

    val viewModel: ProfileViewModel by activityViewModels()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var UserName: String = ""
    private val args: New_Card_FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addCreditCardNumberTxtWatcher(
            requireContext(),
            cardType,
            img,
            et_card_number,
            TextInputLayout_cardNumber,
            '-'
        )
        addCreditCardDateTxtWatcher(et_expiry, '/')

        viewModel.getUser(currentUser!!.uid)
        subsribeToObservers()
        btn_pay.setOnClickListener {
            onCheckboxClicked(checkbox)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subsribeToObservers() {
        val fadeInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_continously)
        viewModel.getUserStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { snackbar(it) },
            onLoading = {
                btn_pay.isEnabled = false
                btn_pay.startAnimation(fadeInAnim)
            }
        ) { user ->
            btn_pay.clearAnimation()
            fadeInAnim.cancel()
            fadeInAnim.reset()
            btn_pay.isEnabled = true
            UserName = user.name
            val decimalFormat = DecimalFormat("#,###,###")
            btn_pay.text = "Pay " + "₦" + decimalFormat.format(args.totalAmount.toDouble())
        })
        viewModel.insertWalletStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { snackbar(it) },
            onLoading = {}
        ) {})
        viewModel.insertWalletStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {},
            onError = { snackbar(it) }
        ) {
            findNavController().navigate(
                New_Card_FragmentDirections.actionNewCardFragmentToBottomNavFragmentDialog(
                    et_card_number.text.toString().replace("-", ""),
                    et_expiry.text.toString().replace("/", ""),
                    et_cvv.text.toString(),
                    args.totalAmount
                )
            )
        })
    }

    private fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.checkbox -> {
                    if (checked) {
                        viewModel.insertWallet(
                            Wallet(
                                cardName = UserName,
                                cardNumber = et_card_number.text.toString().replace("-", ""),
                                expiryDate = et_expiry.text.toString().replace("/", ""),
                                cvv = et_cvv.text.toString(),
                                cardType = cardType.text.toString()
                            )
                        )
                    } else {
                        if (et_card_number.text.toString().isEmpty()) {
                            snackbar(getString(R.string.title_empty_cardNumber))
                        } else if (et_expiry.text.toString().isEmpty()) {
                            snackbar(getString(R.string.title_empty_cardExpiry))
                        } else if (et_cvv.text.toString().isEmpty()) {
                            snackbar(getString(R.string.title_empty_cardCVV))
                        } else if (et_cvv.text.toString().length < 3) {
                            snackbar(getString(R.string.title_invalid_cardCVV))
                        } else if (et_expiry.text.toString().length < 5) {
                            snackbar(getString(R.string.title_invalid_card_date))
                        } else if (et_card_number.text.toString()
                                .isNotEmpty() || et_expiry.text.toString()
                                .isNotEmpty() || et_cvv.text.toString()
                                .isNotEmpty() || et_cvv.text.toString()
                                .length == 3 || et_expiry.text.toString().length == 5
                        ) {
                            findNavController().navigate(
                                New_Card_FragmentDirections.actionNewCardFragmentToBottomNavFragmentDialog(
                                    et_card_number.text.toString().replace("-", ""),
                                    et_expiry.text.toString().replace("/", ""),
                                    et_cvv.text.toString(),
                                    args.totalAmount
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}