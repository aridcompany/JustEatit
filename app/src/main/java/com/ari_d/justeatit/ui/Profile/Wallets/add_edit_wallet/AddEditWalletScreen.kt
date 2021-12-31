package com.ari_d.justeatit.ui.Profile.Wallets.add_edit_wallet

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ari_d.justeatit.util.UiEvent
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@Composable
fun AddEditWalletScreen(
    onPopBackStack: () -> Unit,
    viewModel: AddEditWalletViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.popBackStack -> onPopBackStack()
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                }
                else -> Unit
            }
        }
    }
    Scaffold (
        scaffoldState= scaffoldState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(AddEditWalletEvent.OnSaveWalletClick)
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save"
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                value = viewModel.cardName,
                    onValueChange = {
                        viewModel.onEvent(AddEditWalletEvent.OnCardNameChanged(it))
                    },
                placeholder = {
                    Text(text = "Card Name")
                },
                modifier = Modifier.fillMaxWidth()
                )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = viewModel.cardNumber,
                onValueChange = {
                    viewModel.onEvent(AddEditWalletEvent.OnCardNumberChanged(it))
                },
                placeholder = {
                    Text(text = "Card Number")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = viewModel.cvv,
                onValueChange = {
                    viewModel.onEvent(AddEditWalletEvent.OnCardCvvChanged(it))
                },
                placeholder = {
                    Text(text = "Card CVV")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = viewModel.expiryMonth,
                onValueChange = {
                    viewModel.onEvent(AddEditWalletEvent.OnCardExpiryMonthChanged(it))
                },
                placeholder = {
                    Text(text = "Card Exp. Month")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = viewModel.expiryYear,
                onValueChange = {
                    viewModel.onEvent(AddEditWalletEvent.OnCardExpiryYearChanged(it))
                },
                placeholder = {
                    Text(text = "Card Exp. Year")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}