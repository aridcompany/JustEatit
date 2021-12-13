package com.ari_d.justeatit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.ari_d.justeatit.Adapters.ViewPagerAdapter
import com.ari_d.justeatit.ui.Auth.Auth_Activity
import com.ari_d.justeatit.ui.Main.mainFragments.favorites.FavoritesFragment
import com.ari_d.justeatit.ui.Main.mainFragments.home.HomeFragment
import com.ari_d.justeatit.ui.Main.mainFragments.search.SearchFragment
import com.ari_d.justeatit.ui.Profile.ProfileActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_JustEatIt)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        setUpTabs()
    }
    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(HomeFragment(), getString(R.string.title_home))
        adapter.addFragment(FavoritesFragment(), getString(R.string.title_favorites))
        adapter.addFragment(SearchFragment(), getString(R.string.title_search))
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_home)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_heartimage)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_search)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val User = FirebaseAuth.getInstance()
        val loggedInUser = User.currentUser
        when (item.itemId) {
            R.id.navigation_profile -> if (loggedInUser != null) {
                startActivity(
                    Intent(
                    this,
                    ProfileActivity::class.java
                )
                )
            } else {
                startActivity(Intent(
                    this,
                    Auth_Activity::class.java
                ))
            }
        }
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_menu, menu)
        return true
    }
}