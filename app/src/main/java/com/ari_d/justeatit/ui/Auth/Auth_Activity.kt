package com.ari_d.justeatit.ui.Auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ari_d.justeatit.MainActivity
import com.ari_d.justeatit.R
import com.ari_d.justeatit.ui.Auth.fragments.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Auth_Activity : AppCompatActivity() {

    val authUser = FirebaseAuth.getInstance()
    val user = authUser.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_JustEatIt_NoActionBar)
        setContentView(R.layout.activity_auth)
        if (user != null) {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
        setCurrentFragment(LoginFragment())
    }

    internal fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }
}