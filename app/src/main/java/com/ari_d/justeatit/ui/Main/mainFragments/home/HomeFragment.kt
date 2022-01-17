package com.ari_d.justeatit.ui.Main.mainFragments.home

import android.annotation.SuppressLint
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ari_d.justeatit.Adapters.ProductsAdapter
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.MainActivity
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Details.Details_Activity
import com.ari_d.justeatit.ui.Main.ViewModels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    val homeViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var productsAdapter: ProductsAdapter

    private var curAddedIndex: Int? = null

    private lateinit var auth: FirebaseAuth

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        auth = FirebaseAuth.getInstance()
        homeViewModel.getProducts()
        home_swipe.setOnRefreshListener {
            homeViewModel.getProducts()
            homeViewModel.getCartItemsNo()
            (activity as MainActivity).subscribeToObservers()
        }

        setupRecyclerView()

        productsAdapter.setOnAddToFavoritesClickListener { product, i ->
            curAddedIndex = i
            product.isAddingToFavorites = !product.isAddedToFavorites
            val currentUser = auth.currentUser
            if (currentUser == null) {
                snackbar(getString(R.string.title_signIn_to_continue))
            }
            currentUser?.let {
                homeViewModel.addToFavorites(product)
            }
        }
        productsAdapter.setOnAddToShoppingBagClickListener { product, i ->
            curAddedIndex = i
            product.isAddedToShoppingBag = !product.isAddedToShoppingBag
            val currentUser = auth.currentUser
            if (currentUser == null) {
                snackbar(getString(R.string.title_signIn_to_continue))
            }
            currentUser?.let {
                homeViewModel.addToShoppingBag(product)
            }
        }
        productsAdapter.setOnNavigateToProductsDetailsClickListener { product, i ->
            Intent(requireActivity(), Details_Activity::class.java).also {
                it.putExtra("product_id", product.product_id)
                startActivity(it)
            }
        }
    }

    private fun subscribeToObservers() {
        homeViewModel.getProducts.observe(viewLifecycleOwner, EventObserver(
            onError = {
                empty_layout.isVisible = true
            },
            onLoading = {
                empty_layout.isVisible = false
                products_recycler.isVisible = false
                shimmer_layout.apply {
                    startShimmer()
                }
            }
        ) { products ->
            productsAdapter.products = products
            shimmer_layout.apply {
                stopShimmer()
                isVisible = false
            }
            products_recycler.isVisible = true
            home_swipe.isRefreshing = false

            if (products.isEmpty()) {
                empty_layout.isVisible = true
                products_recycler.isVisible = false
            }
        })

        homeViewModel.addToFavorites.observe(viewLifecycleOwner, EventObserver(
            onError = {
                curAddedIndex?.let { index ->
                    productsAdapter.products[index].isAddingToFavorites = false
                    productsAdapter.notifyItemChanged(index)
                }
                snackbar(it)
                progressBar.isVisible = false
            },
            onLoading = {
                curAddedIndex?.let { index ->
                    productsAdapter.products[index].isAddingToFavorites = true
                    productsAdapter.notifyItemChanged(index)
                }
                progressBar.isVisible = true
            }
        ) { isAddedToFavorites ->
            curAddedIndex?.let { index ->
                val uid = FirebaseAuth.getInstance().uid!!
                productsAdapter.products[index].apply {
                    this.isAddedToFavorites = isAddedToFavorites
                    isAddingToFavorites = false
                    if (isAddedToFavorites) {
                        favoritesList += uid
                        snackbar(getString(R.string.title_added_to_favorites))
                    } else {
                        favoritesList -= uid
                        snackbar(getString(R.string.title_removed_from_favorites))
                    }
                }
                productsAdapter.notifyItemChanged(index)
            }
            progressBar.isVisible = false
        })

        homeViewModel.addToShoppingBag.observe(viewLifecycleOwner, EventObserver(
            onError = {
                curAddedIndex?.let { index ->
                    productsAdapter.products[index].isAddingToShoppingBag = false
                    productsAdapter.notifyItemChanged(index)
                }
                snackbar(it)
                progressBar.isVisible = false
            },
            onLoading = {
                curAddedIndex?.let { index ->
                    productsAdapter.products[index].isAddingToShoppingBag = true
                    productsAdapter.notifyItemChanged(index)
                }
                progressBar.isVisible = true
            }
        ) { isAddedToShoppingBag ->
            curAddedIndex?.let { index ->
                val uid = FirebaseAuth.getInstance().uid!!
                productsAdapter.products[index].apply {
                    this.isAddedToShoppingBag = isAddedToShoppingBag
                    isAddingToShoppingBag = false
                    if (isAddedToShoppingBag) {
                        shoppingBagList += uid
                        snackbar(getString(R.string.title_added_to_shopping_bag))
                    } else {
                        shoppingBagList -= uid
                        snackbar(getString(R.string.title_removed_from_shopping_bag))
                    }
                }
                productsAdapter.notifyItemChanged(index)
            }
            homeViewModel.getCartItemsNo()
            (activity as MainActivity).subscribeToObservers()
            progressBar.isVisible = false
        })
    }

    private fun setupRecyclerView() = products_recycler.apply {
        layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        adapter = productsAdapter
        itemAnimator = null
    }
}