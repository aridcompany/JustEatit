package com.ari_d.justeatit.ui.Auth.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ari_d.justeatit.R
import com.ari_d.justeatit.ui.Auth.Auth_Activity
import com.ari_d.justeatit.Extensions.slideUpViews
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Auth.ViewModels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_register.*

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register){

    val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subScribeToObservers()

        btn_register.setOnClickListener {
            viewModel.register(
                TextInputEditText_email.text.toString().replace(" ", ""),
                TextInputEditText_name.text.toString(),
                TextInputEditText_password.text.toString()
            )
        }
        slideUpViews(
            requireContext(),
            btn_back,
            textView,
            TextInputEditText_name,
            TextInputEditText_email,
            TextInputEditText_password,
            textView2,
            btn_register,
            btn_navigation_signIn
        )

        btn_back.setOnClickListener {
            requireActivity().finish()
        }


        val login = LoginFragment()

        btn_navigation_signIn.setOnClickListener {
            (activity as Auth_Activity).setCurrentFragment(login)
        }
    }

    private fun subScribeToObservers() {
        viewModel.registerStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
                progressBar.isVisible = false
                btn_register.isVisible = true
            },
            onLoading = {
                progressBar.isVisible = true
                btn_register.isVisible = false
            }
        ){
            snackbar(getString(R.string.title_registration_successful))
            progressBar.isVisible = false
            btn_register.isVisible = true
        })
    }
}