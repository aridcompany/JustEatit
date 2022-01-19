package com.ari_d.justeatit.ui.Details

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.ari_d.justeatit.Adapters.Products_Details_ViewPager_Adapter
import com.ari_d.justeatit.Extensions.MyBounceInterpolator
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.Constants
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Cart.CartActivity
import com.ari_d.justeatit.ui.Details.ViewModels.DetailsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class Details_Activity : AppCompatActivity() {

    val viewModel: DetailsViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    private lateinit var product_id: String

    private val stock = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_JustEatIt)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        product_id = intent.getStringExtra("product_id").toString()
        viewModel.setUiInterface(product_id)
        subscribeToObservers()
        auth = FirebaseAuth.getInstance()

        btn_like.setOnClickListener {
            viewModel.addToFavorites(product_id)
        }

        btn_add_to_bag.setOnClickListener {
            viewModel.addToShoppingBag(product_id)
        }

        var job: Job? = null
        btn_increase.setOnClickListener {
            val value = txt_cart_value.text.toString().toInt() + 1
            txt_cart_value.text = value.toString()
            job?.cancel()
            job = lifecycleScope.launch {
                delay(Constants.SEARCH_TIME_DELAY)
                viewModel.increaseCartNo(txt_cart_value.text.toString(), product_id)
            }
        }

        btn_decrease.setOnClickListener {
            val value = txt_cart_value.text.toString().toInt() - 1
            txt_cart_value.text = value.toString()
            job?.cancel()
            job = lifecycleScope.launch {
                delay(Constants.SEARCH_TIME_DELAY)
                viewModel.decreaseCartNo(txt_cart_value.text.toString(), product_id)
            }
        }
    }

    private fun subscribeToObservers() {
        val myAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        val interpolator = MyBounceInterpolator(0.2, 20.0)
        myAnim.interpolator = interpolator

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
                getString(R.string.title_error_loading)
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
                stock.clear()
                stock.add(product.stock.toInt())
                if (auth.currentUser!!.uid in product.favoritesList)
                    btn_like.setImageResource(R.drawable.ic_baseline_favorite_24)
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
                if (auth.currentUser!!.uid in product.shoppingBagList) {
                    btn_add_to_bag.isVisible = false
                    increase_layout.isVisible = true
                    viewModel.getCartProductDetails(product_id)
                }
            }

        })
        viewModel.addToFavoritesStatus.observe(this, EventObserver(
            onLoading = {
                progressBar.isVisible = true
                btn_like.isEnabled = false
            },
            onError = {
                progressBar.isVisible = false
                btn_like.isEnabled = true
            }
        ) { isAddedToFavorites ->
            progressBar.isVisible = false
            btn_like.isEnabled = true
            btn_like.animation = myAnim
            if (isAddedToFavorites) {
                btn_like.setImageResource(R.drawable.ic_baseline_favorite_24)
                btn_like.animation = myAnim
            } else {
                btn_like.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                btn_like.animation = myAnim
            }
        })
        viewModel.addToShoppingBagStatus.observe(this, EventObserver(
            onLoading = {
                btn_add_to_bag.isVisible = false
                progressBar2.isVisible = true
            },
            onError = {
                btn_add_to_bag.isVisible = true
                progressBar2.isVisible = false
            }
        ) {
            btn_add_to_bag.isVisible = false
            increase_layout.isVisible = true
            progressBar2.isVisible = false
            viewModel.getCartProductDetails(product_id)
        })
        viewModel.getCartProductDetailsStatus.observe(this, EventObserver(
            onLoading = {
                progressBar.isVisible = true
                btn_decrease.isEnabled = false
                btn_increase.isEnabled = false
            },
            onError = {
                progressBar.isVisible = false
                btn_decrease.isEnabled = false
                btn_increase.isEnabled = false
            }
        ) { quantity ->
            progressBar.isVisible = false
            txt_cart_value.text = quantity.toString()
            txt_cart_value.animation = myAnim
            btn_decrease.isEnabled = true
            btn_increase.isEnabled = true
        })
        viewModel.increaseCartNumberStatus.observe(this, EventObserver(
            onLoading = {
                btn_decrease.isEnabled = false
                btn_increase.isEnabled = false
                progressBar.isVisible = true
            },
            onError = {
                btn_decrease.isEnabled = true
                btn_increase.isEnabled = true
                progressBar.isVisible = false
            }
        ) { cartNumber ->
            progressBar.isVisible = false
            if (cartNumber.toString() == "1") {
                txt_cart_value.text = cartNumber.toString()
                Toast.makeText(
                    this,
                    getString(R.string.title_product_stock, stock[0]), Toast.LENGTH_SHORT
                ).show()
                txt_cart_value.text = ""
                viewModel.getCartProductDetails(product_id)
            } else {
                txt_cart_value.text = cartNumber.toString()
                btn_decrease.isEnabled = true
                btn_increase.isEnabled = true
            }
        })
        viewModel.decreaseCartNumberStatus.observe(this, EventObserver(
            onLoading = {
                btn_increase.isEnabled = false
                btn_decrease.isEnabled = false
                progressBar.isVisible = true
            },
            onError = {
                btn_increase.isEnabled = true
                btn_decrease.isEnabled = true
                progressBar.isVisible = false
            }
        ) { cartNumber ->
            progressBar.isVisible = false
            if (cartNumber == 0) {
                btn_add_to_bag.isVisible = true
                increase_layout.isVisible = false
                txt_cart_value.text = ""
                viewModel.getCartProductDetails(product_id)
            } else if (cartNumber > 0) {
                txt_cart_value.text = cartNumber.toString()
                btn_decrease.isEnabled = true
                btn_increase.isEnabled = true
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