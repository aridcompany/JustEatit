package com.ari_d.justeatit.ui.Profile.Cart

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.ari_d.justeatit.R
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_JustEatIt_NoActionBar)
        setContentView(R.layout.activity_cart)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_Host_Fragment)
                as NavHostFragment
        navHostFragment.navController
    }
}