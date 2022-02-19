package com.ari_d.justeatit.ui.Profile.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.ari_d.justeatit.Adapters.MainProfileFragmentAdapter
import com.ari_d.justeatit.Adapters.MainProfileFragmentAdapter_About
import com.ari_d.justeatit.Adapters.MainProfileFragmentAdapter_Settings
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Account_Items
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Auth.Auth_Activity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_profile_fragment.*

@AndroidEntryPoint
class main_profile_fragment : Fragment(R.layout.main_profile_fragment) {

    val viewModel: ProfileViewModel by viewModels()
    private lateinit var mainProfileAdapter: MainProfileFragmentAdapter
    private lateinit var mainProfileAdapter_settings: MainProfileFragmentAdapter_Settings
    private lateinit var mainProfileAdapter_about: MainProfileFragmentAdapter_About
    private var webpage_help_url: String = ""
    private var webpage_url: String = ""
    private val currentUser = FirebaseAuth.getInstance().currentUser!!

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainProfileAdapter = MainProfileFragmentAdapter()
        mainProfileAdapter_settings = MainProfileFragmentAdapter_Settings()
        mainProfileAdapter_about = MainProfileFragmentAdapter_About()

        subscribeToObservers()

        viewModel.setNameandEmail()

        btn_logOut.setOnClickListener {
            viewModel.logOut()
        }

        var account_items = mutableListOf(
            Account_Items("Order History", R.drawable.ic_order_history),
            Account_Items("Track Orders", R.drawable.ic_track_order),
            Account_Items("Address Book", R.drawable.ic_address_book),
            Account_Items("My Wallet", R.drawable.ic_credit_cards),
        )
        var settings_items = mutableListOf(
            Account_Items("Update My Details", R.drawable.ic_update_details)
        )
        var about_items = mutableListOf(
            Account_Items(getString(R.string.title_feedback), R.drawable.ic_feedback),
            Account_Items(getString(R.string.title_help), R.drawable.ic_help)
        )
        mainProfileAdapter_settings.setOnUpdateDetailsClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                main_profile_fragmentDirections.actionMainProfileFragmentToUpdateDetailsFragment()
            )
        }

        mainProfileAdapter.setOnWalletClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                main_profile_fragmentDirections.actionMainProfileFragmentToMyWalletFragment()
            )
        }

        mainProfileAdapter.setOnAddressBookClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                main_profile_fragmentDirections.actionMainProfileFragmentToAddressBookFragment()
            )
        }

        mainProfileAdapter_about.setOnFeedbackClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                main_profile_fragmentDirections.actionMainProfileFragmentToFeedBackFragment()
            )
        }

        mainProfileAdapter_about.setOnHelpClickListener {
            viewModel.getHelpUrl()
        }

        text_copyright.setOnClickListener {
            viewModel.getUrl()
        }

        mainProfileAdapter.setOnMyOrdersClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                main_profile_fragmentDirections.actionMainProfileFragmentToMyOrdersFragment()
            )
        }

        mainProfileAdapter.setOnTrackMyOrdersClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                main_profile_fragmentDirections.actionMainProfileFragmentToTrackMyOrdersFragment()
            )
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
        recycler_about.apply {
            mainProfileAdapter_about.items = about_items
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mainProfileAdapter_about
            isNestedScrollingEnabled = false
            itemAnimator = null
        }
    }

    private fun subscribeToObservers() {
        viewModel.setNameStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {},
            onLoading = {}
        ) { user ->
            user_email.text = currentUser.email
            if (user.name == "null")
                user_name.text = getString(R.string.title_welcome)
            else
                user_name.text = user.name
        })

        viewModel.logOutStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {},
            onLoading = {},
        ) {
            Intent(requireContext(), Auth_Activity::class.java).also {
                startActivity(it)
                requireActivity().finish()
            }
        })
        viewModel.getHelpUrlStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { snackbar(getString(R.string.title_unknown_error_occurred)) },
            onLoading = {}
        ) {
            webpage_help_url = it.webpage_help_url
            openUrl(webpage_help_url)
        })
        viewModel.getUrlStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { snackbar(getString(R.string.title_unknown_error_occurred)) },
            onLoading = {}
        ) {
            webpage_url = it.webpage_url
            openUrl(webpage_url)
        })
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openUrl(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            requireContext().startActivity(intent)
        }
    }
}