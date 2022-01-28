package com.ari_d.justeatit.ui.Profile.Wallets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.ari_d.justeatit.R
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ari_d.justeatit.other.walletEvent
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.ari_d.justeatit.util.UiEvent
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@Composable
fun WalletListScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val wallets = viewModel.wallets.collectAsState(initial = emptyList())
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(walletEvent.onUndoDeleteClick)
                    }
                }
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }
        }
    }
    Scaffold (
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(walletEvent.onAddWalletClick)
            },
            backgroundColor = colorResource(id = R.color.accent)) {
                Icon (
                    painter = painterResource(id = R.drawable.ic_add_wallet),
                    contentDescription = "Add"
                )
            }
        }
            ){
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(wallets.value) { wallet ->
                CreditCardItem(
                    wallet = wallet,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    imagePainter = painterResource(id = R.drawable.credit_card)
                )
            }
        }
    }
}