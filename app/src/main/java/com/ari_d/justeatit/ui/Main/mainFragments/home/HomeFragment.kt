package com.ari_d.justeatit.ui.Main.mainFragments.home

import android.annotation.SuppressLint
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
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
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    val homeViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var productsAdapter: ProductsAdapter

    private var curAddedIndex: Int? = null

    private lateinit var auth: FirebaseAuth

    @InternalCoroutinesApi
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        auth = FirebaseAuth.getInstance()
        shimmer_layout?.apply {
            stopShimmer()
            isVisible = true
        }
        getPosts()
        homeViewModel.getCartItemsNo()
        (activity as MainActivity).subscribeToObservers()
        home_swipe.setOnRefreshListener {
            getPosts()
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
            val currentUser = auth.currentUser
            if (currentUser == null) {
                snackbar(getString(R.string.title_signIn_to_continue))
            }
            currentUser?.let {
                Intent(requireActivity(), Details_Activity::class.java).also {
                    it.putExtra("product_id", product.product_id)
                    startActivity(it)
                }
            }
        }
    }

    @InternalCoroutinesApi
    private fun getPosts() {
        lifecycleScope.launch {
            homeViewModel.getPagingFlow().collect {
                productsAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            productsAdapter.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.Loading || it.append is LoadState.Loading) {
                    empty_layout.isVisible = false
                } else if (it.refresh is LoadState.Error) {
                    empty_layout.isVisible = true
                    shimmer_layout.apply {
                        stopShimmer()
                        isVisible = false
                    }
                    home_swipe.isRefreshing = false
                } else if (it.refresh is LoadState.NotLoading || it.append is LoadState.NotLoading) {
                    shimmer_layout.apply {
                        stopShimmer()
                        isVisible = false
                    }
                    home_swipe.isRefreshing = false
                    empty_layout.isVisible = false
                    home_swipe.isRefreshing = false
                }
            }
        }
    }

    private fun subscribeToObservers() {
        homeViewModel.addToFavorites.observe(viewLifecycleOwner, EventObserver(
            onError = {
                curAddedIndex?.let { index ->
                    productsAdapter.peek(index)?.isAddingToFavorites = false
                    productsAdapter.notifyItemChanged(index)
                }
                snackbar(it)
                progressBar.isVisible = false
            },
            onLoading = {
                curAddedIndex?.let { index ->
                    productsAdapter.peek(index)?.isAddingToFavorites = true
                    productsAdapter.notifyItemChanged(index)
                }
                progressBar.isVisible = true
            }
        ) { isAddedToFavorites ->
            curAddedIndex?.let { index ->
                val uid = FirebaseAuth.getInstance().uid!!
                productsAdapter.peek(index)?.apply {
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
                    productsAdapter.peek(index)?.isAddingToShoppingBag = false
                    productsAdapter.notifyItemChanged(index)
                }
                snackbar(it)
                progressBar.isVisible = false
            },
            onLoading = {
                curAddedIndex?.let { index ->
                    productsAdapter.peek(index)?.isAddingToShoppingBag = true
                    productsAdapter.notifyItemChanged(index)
                }
                progressBar.isVisible = true
            }
        ) { isAddedToShoppingBag ->
            curAddedIndex?.let { index ->
                val uid = FirebaseAuth.getInstance().uid!!
                productsAdapter.peek(index)?.apply {
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