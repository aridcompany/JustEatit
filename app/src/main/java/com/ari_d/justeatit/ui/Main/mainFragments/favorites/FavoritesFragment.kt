package com.ari_d.justeatit.ui.Main.mainFragments.favorites

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ari_d.justeatit.Adapters.FavoritesAdapter
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.ui.Details.Details_Activity
import com.ari_d.justeatit.ui.Main.ViewModels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.fragment_favorites.empty_layout
import kotlinx.android.synthetic.main.fragment_favorites.shimmer_layout
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    val viewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var favoritesAdapter: FavoritesAdapter

    private var curAddedIndex: Int? = null

    private lateinit var auth: FirebaseAuth

    @InternalCoroutinesApi
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shimmer_layout.apply {
            startShimmer()
            isVisible = true
        }
        getFavorites()
        auth = FirebaseAuth.getInstance()
        favorites_swipe.setOnRefreshListener {
            if (auth.currentUser == null) {
                snackbar(getString(R.string.title_signIn_to_continue))
                favorites_swipe.isRefreshing = false
            }
            auth.currentUser?.let {
                getFavorites()
            }
        }
    }
    @InternalCoroutinesApi
    private fun getFavorites() {
        lifecycleScope.launch {
            viewModel.getPagingFlowForFavorites().collect {
                favoritesAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            favoritesAdapter.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.Loading || it.append is LoadState.Loading) {
                    empty_layout.isVisible = false
                } else if (it.refresh is LoadState.Error) {
                    empty_layout.isVisible = true
                    shimmer_layout.apply {
                        stopShimmer()
                        isVisible = false
                    }
                    favorites_swipe.isRefreshing = false
                    recycler_favorites.isVisible = false
                } else if (it.refresh is LoadState.NotLoading || it.append is LoadState.NotLoading) {
                    shimmer_layout.apply {
                        stopShimmer()
                        isVisible = false
                    }
                    favorites_swipe.isRefreshing = false
                    recycler_favorites.isVisible = true
                }
            }
        }
        setupRecyclerView()
        favoritesAdapter.setOnNavigateToProductDetailsClickListener { favorite, i ->
            val currentUser = auth.currentUser
            if (currentUser == null) {
                snackbar(getString(R.string.title_signIn_to_continue))
            }
            currentUser?.let {
                Intent(requireActivity(), Details_Activity::class.java).also {
                    it.putExtra("product_id", favorite.product_id)
                    startActivity(it)
                }
            }
        }
    }

    private fun setupRecyclerView() = recycler_favorites.apply {
        adapter = favoritesAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }
}