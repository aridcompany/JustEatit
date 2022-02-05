package com.ari_d.justeatit.ui.Profile.Wallets.add_edit_wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
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
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
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
                    .padding(
                        top = 24.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                shape = RoundedCornerShape(16.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp
                    ),
                shape = RoundedCornerShape(16.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp
                    ),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp
                    ),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val maxChar = 4
                TextField(
                    value = viewModel.expiryDate,
                    modifier = Modifier.fillMaxWidth(0.5f),
                    leadingIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_calendar_today_24),
                                contentDescription = "Expiry Date"
                            )
                        }
                    },
                    onValueChange = {
                        if (it.length <= maxChar)
                        viewModel.onEvent(AddEditWalletEvent.OnCardExpiryDateChanged(it))
                    },
                    label = {
                        Text(text = "Expiry Date")
                    },
                    visualTransformation = DateTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    maxLines = 1,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

class DateTransformation() : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return dateFilter(text)
    }
}

fun dateFilter(text: AnnotatedString): TransformedText {

    val trimmed = if (text.text.length >= 4) text.text.substring(0..3) else text.text
    var out = ""
    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i % 2 == 1 && i < 2) out += "/"
    }

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 1) return offset
            if (offset <= 3) return offset +1
            return 5
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <=2) return offset
            if (offset <=5) return offset -1
            return 4
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}