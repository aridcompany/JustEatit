package com.ari_d.justeatit.ui.Profile.Cart.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ari_d.justeatit.Adapters.PaymentViewPagerAdapter
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Wallet
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_payment.*
import javax.inject.Inject

@AndroidEntryPoint
class Payment_Fragment : Fragment(R.layout.fragment_payment) {

    val viewModel: ProfileViewModel by activityViewModels()
    @Inject
    lateinit var paymentAdapter: PaymentViewPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllWallets()
        subscribeToObservers()
        btn_new_card.setOnClickListener {
            findNavController().navigate(
                Payment_FragmentDirections.actionPaymentFragmentToNewCardFragment()
            )
        }
        paymentAdapter.setOnGetWalletDetailsClickListener { wallet, i, view ->
            Toast.makeText(requireContext(), wallet.cardNumber, Toast.LENGTH_LONG).show()
        }
    }

    private fun subscribeToObservers() {
        viewModel.getAllWalletsStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {},
            onLoading = {}
        ) {
            if (it.isEmpty()) {
                empty_wallet.isVisible = true
                textView11.isVisible = false
            } else
                setUpViewPager(it)
        })
    }

    private fun setUpViewPager(wallets: List<Wallet>) = viewPager.apply {
        paymentAdapter.wallets = wallets
        val viewPagerAdapter = paymentAdapter
        adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, this) { tab, position ->
        }.attach()
    }
}