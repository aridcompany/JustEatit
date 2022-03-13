package com.ari_d.justeatit.ui.Cart.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ari_d.justeatit.R
import kotlinx.android.synthetic.main.fragment_main_cart.*

class Main_Cart_Fragment: Fragment(R.layout.fragment_main_cart) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_checkout.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                Main_Cart_FragmentDirections.actionMainCartFragmentToDeliveryFragment()
            )
        }
        btn_back.setOnClickListener {
          requireActivity().finish()
        }
    }
}