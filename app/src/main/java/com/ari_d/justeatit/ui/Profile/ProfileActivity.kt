package com.ari_d.justeatit.ui.Profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.ari_d.justeatit.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_JustEatIt_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(findViewById(R.id.toolbar))

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_Host_Fragment)
                as NavHostFragment
        navHostFragment.navController
    }
}