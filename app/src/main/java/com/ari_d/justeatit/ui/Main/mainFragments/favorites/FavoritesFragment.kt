package com.ari_d.justeatit.ui.Main.mainFragments.favorites

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ari_d.justeatit.Adapters.FavoritesAdapter
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Details.Details_Activity
import com.ari_d.justeatit.ui.Main.ViewModels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.fragment_favorites.empty_layout
import kotlinx.android.synthetic.main.fragment_favorites.shimmer_layout
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    val viewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var favoritesAdapter: FavoritesAdapter

    private var curAddedIndex: Int? = null

    private lateinit var auth: FirebaseAuth

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        auth = FirebaseAuth.getInstance()
        viewModel.getFavorites()
        favorites_swipe.setOnRefreshListener {
            viewModel.getFavorites()
        }

        setupRecyclerView()
        favoritesAdapter.setOnNavigateToProductDetailsClickListener { favorite, i ->
            Intent(requireActivity(), Details_Activity::class.java).also {
                it.putExtra("product_id", favorite.product_id)
                startActivity(it)
            }
        }
    }

    private fun setupRecyclerView() = recycler_favorites.apply {
        adapter = favoritesAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }

    private fun subscribeToObservers() {
        viewModel.getFavorites.observe(viewLifecycleOwner, EventObserver(
            onError = {
                empty_layout.isVisible = true
            },
            onLoading = {
                empty_layout.isVisible = false
                recycler_favorites.isVisible = false
                shimmer_layout.apply {
                    startShimmer()
                }
            }
        ) { favorites ->
            favoritesAdapter.favorites = favorites
            shimmer_layout.apply {
                stopShimmer()
                isVisible = false
            }
            recycler_favorites.isVisible = true
            favorites_swipe.isRefreshing = false
            if (favorites.isEmpty()) {
                empty_layout.isVisible = true
                recycler_favorites.isVisible = false
            }
        })
    }
}