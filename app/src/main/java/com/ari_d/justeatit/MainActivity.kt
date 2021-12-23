package com.ari_d.justeatit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ari_d.justeatit.Adapters.ViewPagerAdapter
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Auth.Auth_Activity
import com.ari_d.justeatit.ui.Main.ViewModels.MainViewModel
import com.ari_d.justeatit.ui.Main.mainFragments.favorites.FavoritesFragment
import com.ari_d.justeatit.ui.Main.mainFragments.home.HomeFragment
import com.ari_d.justeatit.ui.Main.mainFragments.search.SearchFragment
import com.ari_d.justeatit.ui.Profile.ProfileActivity
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_JustEatIt)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        setUpTabs()
        viewModel.getCartItemsNo()
        subscribeToObservers()
    }
    internal fun subscribeToObservers() {
        viewModel.cartNo.observe(this, EventObserver(
            onError = {},
            onLoading = {},
        ){
            setCartBadgeNumber(it)
        })
    }

    internal fun setCartBadgeNumber(cartNo: Int) {
        btn_cart.getViewTreeObserver().addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onGlobalLayout() {
                val badgeDrawable = BadgeDrawable.create(this@MainActivity)
                badgeDrawable.number = cartNo
                //Important to change the position of the Badge
                badgeDrawable.horizontalOffset = 30
                badgeDrawable.verticalOffset = 20
                BadgeUtils.attachBadgeDrawable(badgeDrawable, btn_cart, null)
                btn_cart.getViewTreeObserver().removeOnGlobalLayoutListener(this)
            }
        })
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