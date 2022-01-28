package com.ari_d.justeatit.ui.Profile.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ari_d.justeatit.Extensions.alertDialog
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_update_details.*

@AndroidEntryPoint
class UpdateDetailsFragment : Fragment(R.layout.fragment_update_details){

    val viewModel: ProfileViewModel by viewModels()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        TextInputEditText_email.setText(currentUser!!.email)

        btn_back.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                UpdateDetailsFragmentDirections.actionUpdateDetailsFragmentToMainProfileFragment()
            )
        }

        btn_update_details.setOnClickListener {
            viewModel.updateUserDetails(
                TextInputEditText_name.text.toString()
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.updateUserDetailsStautus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                progressBar.isVisible = false
                btn_update_details.isVisible = true
                snackbar(it)
            },
            onLoading = {
                progressBar.isVisible = true
                btn_update_details.isVisible = false
            }
        ){
            progressBar.isVisible = false
            btn_update_details.isVisible = true
            alertDialog(
                getString(R.string.title_successfully_updated_details),
                getString(R.string.title_details_updated),
                requireActivity().resources.getDrawable(R.drawable.ic_baseline_person_24)
            )
        })
    }
}