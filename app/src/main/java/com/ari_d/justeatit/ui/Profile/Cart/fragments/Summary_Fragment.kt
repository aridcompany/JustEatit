package com.ari_d.justeatit.ui.Profile.Cart.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ari_d.justeatit.Adapters.ShoppingBagAdapter
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_summary.*
import kotlinx.android.synthetic.main.fragment_summary.progressBar
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class Summary_Fragment: Fragment(R.layout.fragment_summary) {

    val viewModel: ProfileViewModel by activityViewModels()
    @Inject
    lateinit var glide: RequestManager
    @Inject
    lateinit var shoppingBagAdapter: ShoppingBagAdapter
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        subscribeToObservers()
        setupRecyclerView()
        getShoppingBagItems()
        progressBar?.apply {
            isVisible = true
        }
        btn_proceed_to_payment.isEnabled = false
        btn_proceed_to_payment.setOnClickListener {
           findNavController().navigate(
               Summary_FragmentDirections.actionSummaryFragmentToPaymentFragment()
            )
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun getShoppingBagItems() {
        lifecycleScope.launch {
            viewModel.getPagingFlowForShoppingBag().collect {
                shoppingBagAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            shoppingBagAdapter.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.Loading || it.append is LoadState.Loading) { btn_proceed_to_payment.isEnabled = false}
                else if (it.refresh is LoadState.Error) {
                    progressBar.isVisible = false
                    btn_proceed_to_payment.isEnabled = false
                } else if (it.refresh is LoadState.NotLoading || it.append is LoadState.NotLoading) {
                    progressBar.isVisible = false
                    viewModel.calculateTotal()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeToObservers() {
        viewModel.calculateTotalStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                txt_subtotal_amount.text = ""
                txt_shipping_fee.text = ""
                txt_total_amount.text = ""
                btn_proceed_to_payment.isEnabled = false
            },
            onLoading = {
                txt_subtotal_amount.text = ""
                txt_shipping_fee.text = ""
                txt_total_amount.text = ""
                btn_proceed_to_payment.isEnabled = false
            }
        ) {
            val decimalFormat = DecimalFormat("#,###,###")
            btn_proceed_to_payment.isEnabled = true
            txt_shipping_fee.text = "+shipping fee: ₦" + decimalFormat.format(it[0])
            txt_subtotal_amount.text = "₦" + decimalFormat.format(it[1].toDouble())
            txt_total_amount.text = "₦" + decimalFormat.format(it[2].toDouble())
        })
    }

    private fun setupRecyclerView() = recycler_cart.apply {
        adapter = shoppingBagAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}