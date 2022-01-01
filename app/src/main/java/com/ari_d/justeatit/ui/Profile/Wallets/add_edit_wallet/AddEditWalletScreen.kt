package com.ari_d.justeatit.ui.Profile.Wallets.add_edit_wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ari_d.justeatit.R
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
            },
            backgroundColor = colorResource(id = R.color.accent)
            ) {
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
                leadingIcon = {
                              IconButton(onClick = { /*TODO*/ }) {
                                  Icon(
                                      painter = painterResource(id = R.drawable.ic_baseline_person_24),
                                      contentDescription = "Card Holder's Name"
                                  )
                              }
                },
                singleLine = true,
                maxLines = 1,
                    onValueChange = {
                        viewModel.onEvent(AddEditWalletEvent.OnCardNameChanged(it))
                    },
                label = {
                    Text(text = "CardHolder's Name")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = viewModel.cardNumber,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                maxLines = 1,
                leadingIcon = {
                              IconButton(onClick = { /*TODO*/ }) {
                                  Icon(
                                      painter = painterResource(id = R.drawable.ic_baseline_credit_card_24),
                                      contentDescription = "Card Number"
                                  )
                              }
                },
                onValueChange = {
                    viewModel.onEvent(AddEditWalletEvent.OnCardNumberChanged(it))
                },
                label = {
                    Text(text = "Card Number")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = viewModel.cvv,
                leadingIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cvv),
                            contentDescription = "Card Number"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                maxLines = 1,
                onValueChange = {
                    viewModel.onEvent(AddEditWalletEvent.OnCardCvvChanged(it))
                },
                label = {
                    Text(text = "Card CVV")
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
           Row(
               modifier = Modifier.fillMaxWidth(),
               horizontalArrangement = Arrangement.SpaceAround
           ) {
               TextField(
                   value = viewModel.expiryMonth,
                   modifier = Modifier.width(160.dp),
                   leadingIcon = {
                       IconButton(onClick = { /*TODO*/ }) {
                           Icon(
                               painter = painterResource(id = R.drawable.ic_baseline_calendar_today_24),
                               contentDescription = "Card Number"
                           )
                       }
                   },
                   onValueChange = {
                       viewModel.onEvent(AddEditWalletEvent.OnCardExpiryMonthChanged(it))
                   },
                   label = {
                       Text(text = "Exp. Month")
                   },
                   keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                   singleLine = true,
                   maxLines = 1
               )
               TextField(
                   value = viewModel.expiryYear,
                   modifier = Modifier.width(130.dp),
                   leadingIcon = {
                       IconButton(onClick = { /*TODO*/ }) {
                           Icon(
                               painter = painterResource(id = R.drawable.ic_baseline_calendar_today_24),
                               contentDescription = "Card Number"
                           )
                       }
                   },
                   onValueChange = {
                       viewModel.onEvent(AddEditWalletEvent.OnCardExpiryYearChanged(it))
                   },
                   label = {
                       Text(text = "Exp. Yr.")
                   },
                   keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                   singleLine = true,
                   maxLines = 1
               )
           }
        }
    }
}