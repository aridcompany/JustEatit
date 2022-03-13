package com.ari_d.justeatit.ui.Cart.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ari_d.justeatit.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_summary.*

@AndroidEntryPoint
class Summary_Fragment: Fragment(R.layout.fragment_summary) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_proceed_to_payment.setOnClickListener {
           findNavController().navigate(
                Summary_FragmentDirections.actionSummaryFragmentToPaymentFragment()
            )
        }
    }
}