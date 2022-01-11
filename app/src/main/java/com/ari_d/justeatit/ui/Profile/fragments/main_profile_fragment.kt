package com.ari_d.justeatit.ui.Profile.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.ari_d.justeatit.Adapters.MainProfileFragmentAdapter
import com.ari_d.justeatit.Adapters.MainProfileFragmentAdapter_Settings
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Account_Items
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Auth.Auth_Activity
import com.ari_d.justeatit.ui.Profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_profile_fragment.*

@AndroidEntryPoint
class main_profile_fragment : Fragment(R.layout.main_profile_fragment){

    val viewModel: ProfileViewModel by viewModels()
    private lateinit var mainProfileAdapter : MainProfileFragmentAdapter
    private lateinit var mainProfileAdapter_settings : MainProfileFragmentAdapter_Settings

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainProfileAdapter = MainProfileFragmentAdapter()
        mainProfileAdapter_settings = MainProfileFragmentAdapter_Settings()

        subscribeToObservers()

        viewModel.setNameandEmail(
            requireContext().getString(R.string.title_welcome),
            txt_username,
            requireContext().getString(R.string.exclaim),
            txt_useremail
        )

        btn_logOut.setOnClickListener {
            viewModel.logOut()
        }

        var account_items = mutableListOf(
            Account_Items("Orders History", R.drawable.ic_orders),
            Account_Items("Saved Items", R.drawable.ic_saved_items),
            Account_Items("Address Book", R.drawable.ic_addresses),
            Account_Items("Track Orders", R.drawable.ic_track_orders)
        )
        var settings_items = mutableListOf(
            Account_Items("Update My Details", R.drawable.ic_update_my_details),
            Account_Items("My Wallet", R.drawable.ic_wallet),
        )
        mainProfileAdapter_settings.setOnUpdateDetailsClickListener {
            (activity as ProfileActivity).setCurrentFragment(UpdateDetailsFragment())
        }

        mainProfileAdapter_settings.setOnWalletClickListener {
            (activity as ProfileActivity).setCurrentFragment(MyWalletFragment())
        }

        recycler_account.apply {
            mainProfileAdapter.items = account_items
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mainProfileAdapter
            isNestedScrollingEnabled = false
            itemAnimator = null
        }
        recycler_settings.apply {
            mainProfileAdapter_settings.items = settings_items
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mainProfileAdapter_settings
            isNestedScrollingEnabled = false
            itemAnimator = null
        }
    }

    private fun subscribeToObservers() {
        viewModel.setNameStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {},
            onLoading = {}
        ) {
            toolbar.title = txt_username.text.toString()
            toolbar.subtitle = txt_useremail.text.toString()
        })

        viewModel.logOutStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {},
            onLoading = {},
        ){
            Intent(requireContext(), Auth_Activity::class.java).also {
                startActivity(it)
                requireActivity().finish()
            }
        })
    }
}