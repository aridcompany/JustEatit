package com.ari_d.justeatit.ui.Profile.fragments

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
import kotlinx.android.synthetic.main.fragment_address_book.*
import kotlinx.android.synthetic.main.fragment_address_book.shimmer_layout
import javax.inject.Inject

@AndroidEntryPoint
class AddressBook_Fragment : Fragment(R.layout.fragment_address_book) {

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

        address_swipe.setOnRefreshListener {
            viewModel.getAddresses()
        }
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
                AddressBook_FragmentDirections.actionAddressBookFragmentToEditAddressFragment()
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun subscribeToObservers() {
        viewModel.getAddressesStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                recycler_addresses.isVisible = false
                shimmer_layout.isVisible = false
                empty_recycler.isVisible = true
                progressBar.isVisible = false
                address_swipe.isRefreshing = false
                empty_recycler.isVisible = false
            },
            onLoading = {
                recycler_addresses.isVisible = false
                shimmer_layout.isVisible = true
                empty_recycler.isVisible = false
                progressBar.isVisible = false
                empty_recycler.isVisible = false
            }
        ) { addresses ->
            recycler_addresses.isVisible = true
            shimmer_layout.isVisible = false
            empty_recycler.isVisible = false
            progressBar.isVisible = false
            addressAdapter.addressses = addresses
            address_swipe.isRefreshing = false
            if (addresses.isEmpty()) {
                empty_recycler.isVisible = true
                recycler_addresses.isVisible = false
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

    private fun setupRecyclerView() = recycler_addresses.apply {
        adapter = addressAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }
}