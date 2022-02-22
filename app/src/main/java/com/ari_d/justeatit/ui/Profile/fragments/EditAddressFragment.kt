package com.ari_d.justeatit.ui.Profile.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.Constants
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_edit_address.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditAddressFragment : Fragment(R.layout.fragment_edit_address) {

    val viewModel: ProfileViewModel by activityViewModels()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val supportedRegions = mutableListOf<String>()
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSupportedLocations()
        subscribeToObservers()

        var job: Job? = null
        TextInputEditText_postcode.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(Constants.SEARCH_TIME_DELAY)
                editable?.let {
                    supportedRegions?.let {
                        supportedRegions.replaceAll(String::lowercase)
                    }
                    if (!supportedRegions.contains(TextInputEditText_postcode.text.toString().lowercase())) {
                        TextInputEditText_postcode.error = getString(R.string.title_unavailable)
                    }
                }
            }
        }
        TextInputEditText_street_address.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(Constants.SEARCH_TIME_DELAY)
                if (editable!!.isEmpty()) {
                    TextInputEditText_street_address.error = getString(R.string.title_important)
                }
            }
        }
        TextInputEditText_apt_.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(Constants.SEARCH_TIME_DELAY)
                if (editable!!.isEmpty()) {
                    TextInputEditText_apt_.error = getString(R.string.title_important)
                }
            }
        }

        btn_back.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                EditAddressFragmentDirections.actionEditAddressFragmentToAddressBookFragment()
            )
        }
        btn_next_page.setOnClickListener {
            if (
                TextInputEditText_street_address.text.toString().isNotEmpty()
                || TextInputEditText_apt_.text.toString().isNotEmpty()
                || TextInputEditText_postcode.text.toString().isNotEmpty()
                || TextInputEditText_postcode.text.toString() in supportedRegions
            ) {
                findNavController().navigate(
                    R.id.globalActionToEditAddressFragment2,
                    Bundle().apply {
                        putString(
                            "street_address",
                            TextInputEditText_street_address.text.toString()
                        )
                        putString(
                            "apt_suite",
                            TextInputEditText_apt_.text.toString()
                        )
                        putString(
                            "address_city",
                            TextInputEditText_postcode.text.toString()
                        )
                    }
                )
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel.getSupportedLocationStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
                progressBar.isVisible = true
                btn_next_page.isEnabled = false
            },
            onLoading = {
                btn_next_page.isEnabled = false
                progressBar.isVisible = true
            }
        ) { supported_locations ->
            btn_next_page.isEnabled = true
            supportedRegions.addAll(supported_locations)
            progressBar.isVisible = false
        })
    }
}