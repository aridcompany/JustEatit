package com.ari_d.justeatit.ui.Profile.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ari_d.justeatit.Adapters.OrdersAdapter
import com.ari_d.justeatit.Adapters.OrdersAdapter_Test
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Details.Details_Activity
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_my_orders.*
import kotlinx.android.synthetic.main.fragment_my_orders.empty_layout
import kotlinx.android.synthetic.main.fragment_my_orders.shimmer_layout
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyOrdersFragment : Fragment(R.layout.fragment_my_orders) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var ordersAdapter: OrdersAdapter_Test

    private val viewModel: ProfileViewModel by viewModels()

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        subscribeToObservers()
        viewModel.getOrders()
        // getOrders()
        shimmer_layout?.apply {
            startShimmer()
            isVisible = true
        }

        btn_back.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                MyOrdersFragmentDirections.actionMyOrdersFragmentToMainProfileFragment()
            )
        }

        ordersAdapter.setOnNavigateToOrderssDetailsClickListener { orders, i ->
            Intent(requireActivity(), Details_Activity::class.java).also {
                it.putExtra("product_id", orders.productID)
                startActivity(it)
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel.getOrdersStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                empty_layout.isVisible = true
                shimmer_layout.isVisible = false
                snackbar(it)
            },
            onLoading = {}
        ){ orders ->
            shimmer_layout.isVisible = false
            ordersAdapter.products = orders
        })
    }

    /**
    @InternalCoroutinesApi
    private fun getOrders() {
        lifecycleScope.launch {
            viewModel.getPagingFlow().collect {
                ordersAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            ordersAdapter.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.Error) {
                    empty_layout.isVisible = true
                    shimmer_layout.isVisible = false
                } else if (it.refresh is LoadState.NotLoading || it.append is LoadState.NotLoading) {
                    shimmer_layout.isVisible = false
                }
            }
        }
    }
    **/

    private fun setupRecyclerView() = recycler_my_orders.apply {
        adapter = ordersAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}