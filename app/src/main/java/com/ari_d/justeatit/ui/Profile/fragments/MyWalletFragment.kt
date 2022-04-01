package com.ari_d.justeatit.ui.Profile.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ari_d.justeatit.Adapters.MyWalletAdapter
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_my_wallet.*
import javax.inject.Inject

@AndroidEntryPoint
class MyWalletFragment : Fragment(R.layout.fragment_my_wallet) {

    @Inject
    lateinit var walletAdapter: MyWalletAdapter
    val viewModel : ProfileViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setUpRecyclerView()
        viewModel.getAllWallets()
        swipe.setOnRefreshListener {
            viewModel.getAllWallets()
        }
        walletAdapter.setOnDeleteWalletDetailsClickListener { wallet, i, view_ ->
            val popupMenu = PopupMenu(requireContext(), view_)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_address -> {
                        viewModel.deleteWallet(wallet)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.address_menu)
            popupMenu.show()
        }
        add_wallet.setOnClickListener {
            findNavController().navigate(
                MyWalletFragmentDirections.actionMyWalletFragmentToNewWalletFragment()
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.getAllWalletsStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                empty_wallet.isVisible = true
                progressBar.isVisible = false
                swipe.isRefreshing = false
            },
            onLoading = {
                progressBar.isVisible = true
                empty_wallet.isVisible = false
            }
        ){ wallets ->
            if (wallets.isEmpty()) {
                empty_wallet.isVisible = true
                progressBar.isVisible = false
                swipe.isRefreshing = false
                recycler_wallets.isVisible = false
            } else {
                empty_wallet.isVisible = false
                progressBar.isVisible = false
                swipe.isRefreshing = false
                walletAdapter.wallets = wallets
            }

        })
        viewModel.deleteWalletStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { snackbar(it) },
            onLoading = {}
        ){
            getString(R.string.title_wallet_delete)
            viewModel.getAllWallets()
        })
    }

    private fun setUpRecyclerView() = recycler_wallets.apply {
        adapter = walletAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }
}