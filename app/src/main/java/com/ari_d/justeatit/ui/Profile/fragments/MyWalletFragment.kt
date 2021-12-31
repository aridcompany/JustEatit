package com.ari_d.justeatit.ui.Profile.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.fragment.app.Fragment
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ari_d.justeatit.R
import com.ari_d.justeatit.ui.Profile.Wallets.WalletListScreen
import com.ari_d.justeatit.ui.Profile.Wallets.add_edit_wallet.AddEditWalletScreen
import com.ari_d.justeatit.ui.Profile.Wallets.ui.theme.JustEatItTheme
import com.ari_d.justeatit.util.Routes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class MyWalletFragment : Fragment(R.layout.fragment_my_wallet) {
    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setContent {
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
}