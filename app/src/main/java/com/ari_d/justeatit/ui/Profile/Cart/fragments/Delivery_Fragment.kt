package com.ari_d.justeatit.ui.Profile.Cart.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ari_d.justeatit.Adapters.AddressAdapter
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_delivery.*
import kotlinx.android.synthetic.main.fragment_delivery.add_address
import javax.inject.Inject

@AndroidEntryPoint
class Delivery_Fragment: Fragment(R.layout.fragment_delivery) {

    val viewModel: ProfileViewModel by activityViewModels()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    @Inject
    lateinit var addressAdapter: AddressAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAddresses()
        subscribeToObservers()
        setupRecyclerView()

        addressAdapter.setOnDeleteAddressClickListener { address, i, view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_address -> {
                        viewModel.deleteAddress(address)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.address_menu)
            popupMenu.show()
        }

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
    @SuppressLint("NotifyDataSetChanged")
    private fun subscribeToObservers() {
        viewModel.getAddressesStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                empty_layout.isVisible = true
                progressBar.isVisible = false
            },
            onLoading = {
                empty_layout.isVisible = false
                progressBar.isVisible = true
            }
        ) { addresses ->
            progressBar.isVisible = false
            addressAdapter.addressses = addresses
            if (addresses.isEmpty()) {
                empty_layout.isVisible = true
            }
        })
        viewModel.deleteAddressStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                progressBar.isVisible = false
            },
            onLoading = {
                progressBar.isVisible = true
            }
        ) { address ->
            progressBar.isVisible = false
            snackbar(getString(R.string.title_deleted))
        })
    }

    private fun setupRecyclerView() = recycler_cart.apply {
        adapter = addressAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }
}