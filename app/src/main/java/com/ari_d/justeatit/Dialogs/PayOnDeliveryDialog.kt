package com.ari_d.justeatit.Dialogs

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.fragment_bottom_nav.*
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class PayOnDeliveryDialog : BottomSheetDialogFragment() {

    override fun getTheme() = R.style.NoBackgroundDialogTheme
    private val args: BottomNavFragmentDialogArgs by navArgs()
    val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_nav, container, false)
        view.setBackgroundResource(R.drawable._curved_bottom_layout)
        return view
    }

    @SuppressLint("NewApi")
    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subsribeToObservers()
        viewModel.createOrder(getString(R.string.title_pay_on_delivery))
    }

    private fun subsribeToObservers() {
        val fadeInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        val fadeOutAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        viewModel.createOrderStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
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
                snackbar(it)
            },
            onLoading = { ProgressBar.isVisible = true }
        ) {
            ProgressBar.animation = fadeOutAnim
            fadeOutAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {}

                override fun onAnimationEnd(p0: Animation?) {
                    order_successful.isVisible = true
                    order_successful.animation = fadeInAnim
                    textView11.isVisible = true
                    textView11.animation = fadeInAnim
                }

                override fun onAnimationRepeat(p0: Animation?) {}
            })
            ProgressBar.isVisible = false
        })
    }
}