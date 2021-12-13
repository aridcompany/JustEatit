package com.ari_d.justeatit.ui.Auth.fragments

import android.content.Intent
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
import com.ari_d.justeatit.ui.Profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login){

    val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()


        btn_back.setOnClickListener {
            requireActivity().finish()
        }

        btn_login.setOnClickListener{
            viewModel.login(
                TextInputEditText_email.text.toString(),
                TextInputEditText_password.text.toString()
            )
        }

        slideUpViews(
            requireContext(),
            btn_back,
            textView,
            TextInputEditText_email,
            TextInputEditText_password,
            textView2,
            btn_login,
            btn_navigation_signUp,
            btn_navigation_forgotPassword
        )

        val register = RegisterFragment()
        val resetPassword = ResetPasswordFragment()

        btn_navigation_signUp.setOnClickListener {
            (activity as Auth_Activity).setCurrentFragment(register)
        }
        btn_navigation_forgotPassword.setOnClickListener {
            (activity as Auth_Activity).setCurrentFragment(resetPassword)
        }
    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                progressBar.isVisible = false
                btn_login.isVisible = true
                snackbar(it)
            },
            onLoading = {
                progressBar.isVisible = true
                btn_login.isVisible = false
            }
        ) {
            progressBar.isVisible = false
            btn_login.isVisible = true
            Intent(requireContext(), ProfileActivity::class.java).also {
                startActivity(it)
                requireActivity().finish()
            }
        })
    }
}