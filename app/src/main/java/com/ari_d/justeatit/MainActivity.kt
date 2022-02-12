package com.ari_d.justeatit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ari_d.justeatit.Adapters.ViewPagerAdapter
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Auth.Auth_Activity
import com.ari_d.justeatit.ui.Cart.CartActivity
import com.ari_d.justeatit.ui.Main.ViewModels.MainViewModel
import com.ari_d.justeatit.ui.Main.mainFragments.favorites.FavoritesFragment
import com.ari_d.justeatit.ui.Main.mainFragments.home.HomeFragment
import com.ari_d.justeatit.ui.Main.mainFragments.search.SearchFragment
import com.ari_d.justeatit.ui.Profile.ProfileActivity
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_JustEatIt)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        setUpTabs()
        viewModel.getCartItemsNo()
        subscribeToObservers()

        btn_cart.setOnClickListener {
            if (auth.currentUser != null) {
                startActivity(
                    Intent(
                        this,
                        CartActivity::class.java
                    )
                )
            } else {
                Toast.makeText(
                    this, getString(R.string.title_signIn_to_continue),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    internal fun subscribeToObservers() {
        viewModel.cartNo.observe(this, EventObserver(
            onError = {},
            onLoading = {},
        ) {
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
        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(HomeFragment())
        adapter.addFragment(FavoritesFragment())
        adapter.addFragment(SearchFragment())
        viewPager.adapter = adapter
        TabLayoutMediator(tabs, viewPager) {tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Home"
                    tab.setIcon(R.drawable.ic_home)
                }
                1 -> {
                    tab.text = "Favorites"
                    tab.setIcon(R.drawable.ic_heartimage)
                }
                2 -> {
                    tab.text = "Search"
                    tab.setIcon(R.drawable.ic_search)
                }
            }
        }.attach()
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
                startActivity(
                    Intent(
                        this,
                        Auth_Activity::class.java
                    )
                )
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.tool_bar_menu, menu)
        return true
    }
}