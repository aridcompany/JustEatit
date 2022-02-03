package com.ari_d.justeatit.ui.Profile.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.Constants
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_edit_address2.*
import kotlinx.android.synthetic.main.fragment_edit_address2.btn_back
import kotlinx.android.synthetic.main.fragment_edit_address2.progressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditAddressFragment2 : Fragment(R.layout.fragment_edit_address2) {

    val viewModel: ProfileViewModel by activityViewModels()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    private val args: EditAddressFragment2Args by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var job: Job? = null
        TextInputEditText_phone.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(Constants.SEARCH_TIME_DELAY)
                editable?.let {
                    if (it.isEmpty()) {
                        TextInputEditText_phone.error = getString(R.string.title_important)
                    }
                }
            }
        }
        btn_save_address.setOnClickListener {
            if (TextInputEditText_phone.text.toString().isEmpty())
                getString(R.string.title_empty_input)
            else if (TextInputEditText_phone.text.toString().isNotEmpty())
                viewModel.createAddress(
                    args.streetAddress,
                    args.aptSuite,
                    args.addressCity,
                    TextInputEditText_phone.text.toString(),
                    TextInputEditText_additional_phone.text.toString()
                )
        }
        btn_back.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                EditAddressFragment2Directions.actionEditAddressFragment22ToEditAddressFragment()
            )
        }
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.createAddressStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                btn_save_address.isEnabled = true
                progressBar.isVisible = false
            },
            onLoading = {
                btn_save_address.isEnabled = false
                progressBar.isVisible = true
            }
        ) { address ->
            progressBar.isVisible = false
            btn_save_address.isEnabled = true
            snackbar(getString(R.string.title_address_saved_successfully))
        })
    }
}