package com.ari_d.justeatit.ui.Profile.Cart.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ari_d.justeatit.Adapters.ShoppingBagAdapter
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Details.Details_Activity
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main_cart.*
import kotlinx.android.synthetic.main.fragment_main_cart.btn_back
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class Main_Cart_Fragment : Fragment(R.layout.fragment_main_cart) {

    @Inject
    lateinit var glide: RequestManager
    @Inject
    lateinit var shoppingBagAdapter: ShoppingBagAdapter
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setupRecyclerView()
        subscribeToObservers()
        getShoppingBagItems()
        swipe.setOnRefreshListener {
            getShoppingBagItems()
        }
        progressBar?.apply {
            isVisible = true
        }
        btn_checkout.isEnabled = false

        btn_checkout.setOnClickListener {
            viewModel.checkShoppingBagForUnavailableProducts()
        }
        btn_back.setOnClickListener {
            requireActivity().finish()
        }
        shoppingBagAdapter.setOnNavigateToProductsDetailsClickListener { product, i ->
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

    @OptIn(InternalCoroutinesApi::class)
    private fun getShoppingBagItems() {
        lifecycleScope.launch {
            viewModel.getPagingFlowForShoppingBag().collect {
                shoppingBagAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            shoppingBagAdapter.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.Loading || it.append is LoadState.Loading) { btn_checkout.isEnabled = false}
                else if (it.refresh is LoadState.Error) {
                    empty_layout.isVisible = true
                    progressBar.isVisible = false
                    btn_checkout.isEnabled = false
                } else if (it.refresh is LoadState.NotLoading || it.append is LoadState.NotLoading) {
                    progressBar.isVisible = false
                    viewModel.calculateTotal()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeToObservers() {
        viewModel.checkShoppingBagForUnavailableProductsStatus.observe(viewLifecycleOwner,
            EventObserver(
                onError = {
                    progressBar.isVisible = false
                    btn_checkout.isEnabled = false
                    snackbar(it)
                },
                onLoading = {
                    progressBar.isVisible = true
                    btn_checkout.isEnabled = false
                }
            ) { isAvailable ->
                btn_checkout.isEnabled = true
                progressBar.isVisible = false
                if (isAvailable) {
                    if (findNavController().previousBackStackEntry != null) {
                        findNavController().popBackStack()
                    } else
                        findNavController().navigate(
                            Main_Cart_FragmentDirections.actionMainCartFragmentToDeliveryFragment()
                        )
                } else if (!isAvailable)
                    snackbar(getString(R.string.title_some_items_are_out_of_stock))
            })
        viewModel.calculateTotalStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                txt_subtotal_amount.text = ""
                btn_checkout.isEnabled = false
            },
            onLoading = {
                txt_subtotal_amount.text = ""
                btn_checkout.isEnabled = false
            }
        ) {
            val fadeInAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            val decimalFormat = DecimalFormat("#,###,###")
            btn_checkout.isEnabled = true
            txt_subtotal_amount.text = "₦" + decimalFormat.format(it[1].toDouble())
            txt_subtotal_amount.animation = fadeInAnim
            swipe.isRefreshing = false
        })
    }

    private fun setupRecyclerView() = recycler_cart.apply {
        adapter = shoppingBagAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}