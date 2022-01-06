package com.ari_d.justeatit.ui.Details

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.ari_d.justeatit.R
import com.ari_d.justeatit.ui.CartActivity

class Details_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_JustEatIt)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
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
}