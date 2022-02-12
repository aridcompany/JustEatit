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
import com.ari_d.justeatit.Adapters.TrackOrdersAdapter
import com.ari_d.justeatit.R
import com.ari_d.justeatit.ui.Details.Details_Activity
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_my_orders.btn_back
import kotlinx.android.synthetic.main.fragment_my_orders.shimmer_layout
import kotlinx.android.synthetic.main.fragment_track_orders.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrackMyOrdersFragment: Fragment(R.layout.fragment_track_orders) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var trackordersAdapter: TrackOrdersAdapter

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        getOrders()

        btn_back.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                TrackMyOrdersFragmentDirections.actionTrackMyOrdersFragmentToMainProfileFragment()
            )
        }

        trackordersAdapter.setOnNavigateToOrderssDetailsClickListener { orders, i ->
            Intent(requireActivity(), Details_Activity::class.java).also {
                it.putExtra("product_id", orders.productID)
                startActivity(it)
            }
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun getOrders() {
        lifecycleScope.launch {
            viewModel.getPagingFlowForTracking().collect {
                trackordersAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            trackordersAdapter.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.Loading || it.append is LoadState.Loading) {
                    shimmer_layout.isVisible = true
                } else if (it.refresh is LoadState.Error) {
                    empty_layout.isVisible = true
                    shimmer_layout.isVisible = false
                } else if (it.refresh is LoadState.NotLoading || it.append is LoadState.NotLoading) {
                    shimmer_layout.isVisible = false
                }
            }
        }
    }

    private fun setupRecyclerView() = recycler_track_orders.apply {
        adapter = trackordersAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}