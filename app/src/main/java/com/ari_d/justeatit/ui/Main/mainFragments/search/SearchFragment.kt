package com.ari_d.justeatit.ui.Main.mainFragments.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ari_d.justeatit.Adapters.searchAdapter
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.Constants.SEARCH_TIME_DELAY
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Details.Details_Activity
import com.ari_d.justeatit.ui.Main.ViewModels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    @Inject
    lateinit var productsAdapter: searchAdapter

    private lateinit var auth: FirebaseAuth

    val viewModel: MainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setupRecyclerView()
        subscribeToObservers()

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    viewModel.searchProducts(it.toString())
                }
            }
        }
        productsAdapter.setOnNavigateToProductDetailsClickListener { product, i ->
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

    private fun setupRecyclerView() = rvSearchResults.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = productsAdapter
        itemAnimator = null
    }

    private fun subscribeToObservers() {
        viewModel.searchResults.observe(viewLifecycleOwner, EventObserver(
            onError = {
                searchProgressBar.isVisible = false
                snackbar(it)
                empty_layout.isVisible = true
            },
            onLoading = {
                empty_layout.isVisible = false
                searchProgressBar.isVisible = true
            }
        ){ products ->
            searchProgressBar.isVisible = false
            productsAdapter.products = products

            if (products.isEmpty()) {
                empty_layout.isVisible = true
                rvSearchResults.isVisible = false
            }
        })
    }
}