package com.ari_d.justeatit.ui.Details

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.ari_d.justeatit.R
import com.ari_d.justeatit.ui.Cart.CartActivity
import com.ari_d.justeatit.ui.Details.Fragments.Details_Fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Details_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_JustEatIt)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_Host_Fragment)
                as NavHostFragment
        navHostFragment.navController
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_cart -> startActivity(
                Intent(
                    this,
                    CartActivity::class.java
                )
            )
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        return true
    }

    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.nav_Host_Fragment, fragment)
            commit()
        }
    }
}