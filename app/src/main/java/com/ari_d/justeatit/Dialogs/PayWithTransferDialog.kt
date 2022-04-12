package com.ari_d.justeatit.Dialogs

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_pay_with_transfer.*
import kotlinx.android.synthetic.main.fragment_pay_with_transfer.ProgressBar
import kotlinx.android.synthetic.main.fragment_pay_with_transfer.order_successful
import kotlinx.android.synthetic.main.fragment_pay_with_transfer.payment_error
import kotlinx.android.synthetic.main.fragment_pay_with_transfer.textView11
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class PayWithTransferDialog : BottomSheetDialogFragment() {

    override fun getTheme() = R.style.NoBackgroundDialogTheme
    private val args: BottomNavFragmentDialogArgs by navArgs()
    val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pay_with_transfer, container, false)
        view.setBackgroundResource(R.drawable._curved_bottom_layout)
        return view
    }

    @SuppressLint("NewApi")
    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subsribeToObservers()
        viewModel.getUrl()
        money_sent.setOnClickListener {
            viewModel.createOrder(getString(R.string.title_payment_with_transfer))
        }
        copy_acc_no.setOnClickListener {
            requireContext().copyToClipBoard()
        }
    }

    private fun subsribeToObservers() {
        val fadeInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val fadeOutAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        viewModel.getUrlStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                payment_error.isVisible = true
                payment_error.animation = fadeInAnim
            },
            onLoading = { ProgressBar.isVisible = true }
        ) { payment ->
            ProgressBar.animation = fadeOutAnim
            ProgressBar.isVisible = false
            transfer.isVisible = true
            transfer.animation = fadeInAnim
            acc_no.text = payment.transfer_acc_no
            bank_name.text = payment.transfer_bank_name
        })
        viewModel.createOrderStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
                ProgressBar.animation = fadeOutAnim
                fadeOutAnim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(p0: Animation?) {}

                    override fun onAnimationEnd(p0: Animation?) {
                        payment_error.isVisible = true
                        payment_error.animation = fadeInAnim
                    }

                    override fun onAnimationRepeat(p0: Animation?) {}
                })
                ProgressBar.isVisible = false
            },
            onLoading = {
                ProgressBar.isVisible = true
                transfer.isVisible = false
                money_sent.isEnabled = false
            }
        ) {
            ProgressBar.animation = fadeOutAnim
            order_successful.animation = fadeInAnim
            order_successful.isVisible = true
            textView11.animation = fadeInAnim
            textView11.isVisible = true
            ProgressBar.isVisible = false
        })
    }

    fun Context.copyToClipBoard() {
        val clipboard = getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager
        val clip = ClipData.newPlainText("label", acc_no.text.toString())
        clipboard.setPrimaryClip(clip)
        snackbar(getString(R.string.title_copy_to_clipboard))
    }
}