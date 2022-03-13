package com.ari_d.justeatit.ui.Cart.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ari_d.justeatit.R
import kotlinx.android.synthetic.main.fragment_delivery.*

class Delivery_Fragment: Fragment(R.layout.fragment_delivery) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_address.setOnClickListener {
            findNavController().navigate(
                Delivery_FragmentDirections.actionDeliveryFragmentToEditAddressFragment2()
            )
        }
        btn_summary.setOnClickListener {
            findNavController().navigate(
               Delivery_FragmentDirections.actionDeliveryFragmentToSummaryFragment()
            )
        }
    }
}