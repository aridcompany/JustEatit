package com.ari_d.justeatit.ui.Profile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ari_d.justeatit.Extensions.slideUpViews
import com.ari_d.justeatit.R
import com.ari_d.justeatit.databinding.FragmentMyWalletBinding
import com.ari_d.justeatit.ui.Profile.Wallets.WalletListScreen
import com.ari_d.justeatit.ui.Profile.Wallets.add_edit_wallet.AddEditWalletScreen
import com.ari_d.justeatit.ui.theme.JustEatItTheme
import com.ari_d.justeatit.util.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_my_wallet.*
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class MyWalletFragment : Fragment(R.layout.fragment_my_wallet) {

    private var _binding: FragmentMyWalletBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyWalletBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.composeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                JustEatItTheme {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Routes.WALLET_LIST
                    ) {
                        composable(Routes.WALLET_LIST) {
                            WalletListScreen(
                                onNavigate = {
                                    navController.navigate(it.route)
                                }
                            )
                        }
                        composable(
                            route = Routes.ADD_EDIT_WALLET + "?walletId={walletId}",
                            arguments = listOf(
                                navArgument(name = "walletId") {
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )
                        ){
                            AddEditWalletScreen(onPopBackStack = {
                                navController.popBackStack()
                            })
                        }
                    }
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        slideUpViews(
            requireContext(),
            appBarLayout2,
            compose_view
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}