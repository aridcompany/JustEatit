package com.ari_d.justeatit.ui.Details.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ari_d.justeatit.Adapters.Products_Details_ViewPager_Adapter
import com.ari_d.justeatit.Extensions.MyBounceInterpolator
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.Constants
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Details.ViewModels.DetailsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class Details_Fragment : Fragment(R.layout.fragment_details) {

    val viewModel: DetailsViewModel by activityViewModels()

    private lateinit var auth: FirebaseAuth

    private lateinit var product_id: String

    private var stock: String = ""

    private var contact_no: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        product_id = requireActivity().intent.getStringExtra("product_id").toString()
        viewModel.setUiInterface(product_id)
        subscribeToObservers()
        auth = FirebaseAuth.getInstance()
        tabLayout.background = null

        btn_like.setOnClickListener {
            viewModel.addToFavorites(product_id)
        }

        btn_add_to_bag.setOnClickListener {
            viewModel.addToShoppingBag(product_id)
        }

        btn_call_to_order.setOnClickListener {
            dialContactNo(contact_no)
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

        btn_product_comments.setOnClickListener {
            findNavController().navigate(
                R.id.globalActionToCommentDialog,
                Bundle().apply { putString("productId", product_id) }
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeToObservers() {
        val myAnim: Animation = AnimationUtils.loadAnimation(context, R.anim.bounce)
        val interpolator = MyBounceInterpolator(0.2, 20.0)
        myAnim.interpolator = interpolator

        viewModel.setUiInterfaceStatus.observe(viewLifecycleOwner, EventObserver(
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
        ) { product ->
            viewModel.getProductDetails(product_id)
        })
        viewModel.getProductDetailsStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {},
            onError = {
                getString(R.string.title_error_loading)
            }
        ) { product ->
            if (!product.isAvailable) {
                btn_call_to_order.isEnabled = false
                btn_add_to_bag.isEnabled = false
                scrollView2.isVisible = false
                out_of_stock.isVisible = true
                progressBar2.isVisible = false
            } else {
                progressBar.isVisible = false
                btn_add_to_bag.isVisible = true
                scrollView2.isVisible = true
                progressBar2.isVisible = false
                btn_call_to_order.isVisible = true
                setUpViewPager(product.images)
                btn_add_to_bag.isEnabled = true
                btn_call_to_order.isEnabled = true
                val decimalFormat = DecimalFormat("#,###,###")
                txt_product_name.text = product.name
                txt_product_price.text =
                    getString(R.string.title_naira_sign) + decimalFormat.format(product.price.toInt())
                txt_product_shipping_fee.text =
                    getString(R.string.title_shipping_fee) + decimalFormat.format(product.shipping_fee.toInt())
                txt_product_details.text = product.description
                stock = product.stock
                contact_no = product.contact_no
                if (auth.currentUser!!.uid in product.favoritesList)
                    btn_like.setImageResource(
                        R.drawable.ic_baseline_favorite_24
                    )
                if (auth.currentUser!!.uid in product.shoppingBagList) {
                    btn_add_to_bag.isVisible = false
                    increase_layout.isVisible = true
                    viewModel.getCartProductDetails(product_id)
                }
                if (product.stock.toInt() > 4) {
                    product_stock.isVisible = true
                    product_stock.text = getString(R.string.title_in_stock)
                } else if (product.stock.toInt() < 5) {
                    product_stock.isVisible = true
                    product_stock.text =
                        getString(R.string.title_product_stock, product.stock.toInt())
                }
                when {
                    product.favoritesList.size == 1 -> product_likes.text =
                        getString(R.string.title_like, product.favoritesList.size)
                    product.favoritesList.size > 1 -> product_likes.text =
                        decimalFormat.format(product.favoritesList.size) + " " + getString(R.string.title_likes)
                    product.favoritesList.isEmpty() -> product_likes.text =
                        getString(R.string.title_no_likes)
                }
            }
        })
        viewModel.addToFavoritesStatus.observe(viewLifecycleOwner, EventObserver(
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
        viewModel.addToShoppingBagStatus.observe(viewLifecycleOwner, EventObserver(
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
        viewModel.getCartProductDetailsStatus.observe(viewLifecycleOwner, EventObserver(
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
        viewModel.increaseCartNumberStatus.observe(viewLifecycleOwner, EventObserver(
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
                    context,
                    getString(R.string.title_product_stock, stock.toInt()), Toast.LENGTH_SHORT
                ).show()
                txt_cart_value.text = ""
                viewModel.getCartProductDetails(product_id)
            } else {
                txt_cart_value.text = cartNumber.toString()
                btn_decrease.isEnabled = true
                btn_increase.isEnabled = true
            }
        })
        viewModel.decreaseCartNumberStatus.observe(viewLifecycleOwner, EventObserver(
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

    @SuppressLint("QueryPermissionsNeeded")
    private fun dialContactNo(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null)
            startActivity(intent)
    }
}