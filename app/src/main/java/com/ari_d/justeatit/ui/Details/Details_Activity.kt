package com.ari_d.justeatit.ui.Details

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.ari_d.justeatit.Adapters.Products_Details_ViewPager_Adapter
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Cart.CartActivity
import com.ari_d.justeatit.ui.Details.ViewModels.DetailsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_details.*
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class Details_Activity : AppCompatActivity() {

    val viewModel: DetailsViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    private lateinit var product_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_JustEatIt)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        product_id = intent.getStringExtra("product_id").toString()
        viewModel.setUiInterface(product_id)
        subscribeToObservers()
        auth = FirebaseAuth.getInstance()
    }

    private fun subscribeToObservers() {
        viewModel.setUiInterfaceStatus.observe(this, EventObserver(
            onLoading = {
                progressBar.isVisible = true
                scrollView2.isVisible = false
                btn_add_to_bag.isEnabled = false
                btn_call_to_order.isEnabled = false
                progressBar2.isVisible = true
            },
            onError = {
                getString(R.string.title_unknown_error_occurred)
            }
        ) {
            viewModel.getProductDetails(product_id)
        })
        viewModel.getProductDetailsStatus.observe(this, EventObserver(
            onLoading = {},
            onError = {
                getString(R.string.title_unknown_error_occurred)
            }
        ) { product ->
            progressBar.isVisible = false
            btn_add_to_bag.isVisible = true
            scrollView2.isVisible = true
            progressBar2.isVisible = false
            btn_call_to_order.isVisible = true
            setUpViewPager(product.images)
            if (!product.isAvailable) {
                btn_call_to_order.isEnabled = false
                btn_add_to_bag.isEnabled = false
                scrollView2.isVisible = false
                out_of_stock.isVisible = true
            } else {
                btn_add_to_bag.isEnabled = true
                btn_call_to_order.isEnabled = true
                val decimalFormat = DecimalFormat("#,###,###")
                txt_product_name.text = product.name
                txt_product_price.text =
                    getString(R.string.title_naira_sign) + decimalFormat.format(product.price.toInt())
                txt_product_shipping_fee.text =
                    getString(R.string.title_shipping_fee) + decimalFormat.format(product.shipping_fee.toInt())
                txt_product_details.text = product.description
                if (product.stock.toInt() > 5) return@EventObserver
                product_stock.isVisible = true
                product_stock.text = getString(R.string.title_product_stock, product.stock.toInt())
                if (product.favoritesList.size == 0) {
                } else if (product.favoritesList.size == 1) {
                    product_likes.text =
                        getString(R.string.title_like, product.favoritesList.size)
                } else {
                    product_likes.text =
                        decimalFormat.format(product.favoritesList.size) + getString(R.string.title_likes)
                }
                if (auth.currentUser!!.uid in product.favoritesList)
                    btn_like.setImageResource(R.drawable.ic_baseline_favorite_24)
            }

        })
    }

    private fun setUpViewPager(images: List<String>) = recycler_product_details.apply {
        val viewPagerAdapter = Products_Details_ViewPager_Adapter(images)
        adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, recycler_product_details) { tab, position ->
        }.attach()
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