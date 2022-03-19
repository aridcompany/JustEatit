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
import javax.inject.Inject

@AndroidEntryPoint
class Delivery_Fragment : Fragment(R.layout.fragment_delivery) {

    val viewModel: ProfileViewModel by activityViewModels()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    @Inject
    lateinit var addressAdapter: AddressAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getDefaultAddress()
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
                    R.id.makeDefault_address -> {
                        viewModel.makeAddressDefault(address)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.address_menu2)
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
                btn_summary.isEnabled = false
            },
            onLoading = {
                empty_layout.isVisible = false
                progressBar.isVisible = true
                btn_summary.isEnabled = false
            }
        ) { addresses ->
            addressAdapter.notifyDataSetChanged()
            progressBar.isVisible = false
            if (addresses.isEmpty()) {
                empty_layout.isVisible = true
                btn_summary.isEnabled = false
                recycler_cart.isVisible = false
                recycler_cart2.isVisible = true
            } else {
                recycler_cart.isVisible = true
                recycler_cart2.isVisible = false
                addressAdapter.addressses = addresses
                btn_summary.isEnabled = true
            }
        })
        viewModel.deleteAddressStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { progressBar.isVisible = false },
            onLoading = {
                progressBar.isVisible = true
                btn_summary.isEnabled = false
            }
        ) {
            viewModel.getDefaultAddress()
            progressBar.isVisible = false
            btn_summary.isEnabled = true
            snackbar(getString(R.string.title_deleted))
        })
        viewModel.getDefaultAddressStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { progressBar.isVisible = false },
            onLoading = { progressBar.isVisible = true }
        ) {
            viewModel.getAddresses()
        })
        viewModel.makeDefaultAddressStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                progressBar.isVisible = false
                btn_summary.isEnabled = true
            },
            onLoading = {
                progressBar.isVisible = true
                btn_summary.isEnabled = false
            }
        ) {
            addressAdapter.notifyDataSetChanged()
            progressBar.isVisible = false
            btn_summary.isEnabled = true
            snackbar(getString(R.string.title_now_default))
        })
    }

    private fun setupRecyclerView() = recycler_cart.apply {
        adapter = addressAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }
}