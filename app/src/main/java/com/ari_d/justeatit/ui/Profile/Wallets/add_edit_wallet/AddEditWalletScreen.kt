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
import androidx.compose.ui.text.input.*
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
                visualTransformation = VisualTransformation { number ->
                    when (identifyCardScheme(viewModel.cardNumber)) {
                        CardScheme.AMEX -> formatAmex(number)
                        CardScheme.DINERS_CLUB -> formatDinnersClub(number)
                        CardScheme.MASTERCARD -> formatOtherCardNumbers(number)
                        CardScheme.VISA -> formatOtherCardNumbers(number)
                        CardScheme.VERVE -> formatVerveCard(number)
                        else -> formatOtherCardNumbers(number)
                    }
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
                modifier = Modifier
                    .fillMaxWidth()
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
            if (offset <= 3) return offset + 1
            return 5
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 2) return offset
            if (offset <= 5) return offset - 1
            return 4
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}

enum class CardScheme {
    JCB, AMEX, DINERS_CLUB, VISA, MASTERCARD, DISCOVER, MAESTRO, VERVE, UNKNOWN
}

fun identifyCardScheme(cardNumber: String): CardScheme {
    val jcbRegex = Regex("^(?:2131|1800|35)[0-9]{0,}$")
    val ameRegex = Regex("^3[47][0-9]{0,}\$")
    val dinersRegex = Regex("^3(?:0[0-59]{1}|[689])[0-9]{0,}\$")
    val visaRegex = Regex("^4[0-9]{0,}\$")
    val masterCardRegex = Regex("^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[01]|2720)[0-9]{0,}\$")
    val verveRegex = Regex("^50610[59]0[0-9]{0,}\$")
    val maestroRegex = Regex("^(5[06789]|6)[0-9]{0,}\$")
    val discoverRegex =
        Regex("^(6011|65|64[4-9]|62212[6-9]|6221[3-9]|622[2-8]|6229[01]|62292[0-5])[0-9]{0,}\$")

    val trimmedCardNumber = cardNumber.replace(" ", "")

    return when {
        trimmedCardNumber.matches(jcbRegex) -> CardScheme.JCB
        trimmedCardNumber.matches(ameRegex) -> CardScheme.AMEX
        trimmedCardNumber.matches(dinersRegex) -> CardScheme.DINERS_CLUB
        trimmedCardNumber.matches(visaRegex) -> CardScheme.VISA
        trimmedCardNumber.matches(masterCardRegex) -> CardScheme.MASTERCARD
        trimmedCardNumber.matches(discoverRegex) -> CardScheme.DISCOVER
        trimmedCardNumber.matches(verveRegex) -> CardScheme.VERVE
        trimmedCardNumber.matches(maestroRegex) -> if (cardNumber[0] == '5') CardScheme.MASTERCARD else CardScheme.MAESTRO
        else -> CardScheme.UNKNOWN
    }
}

fun formatAmex(text: AnnotatedString): TransformedText {
//
    val trimmed = if (text.text.length >= 15) text.text.substring(0..14) else text.text
    var out = ""

    for (i in trimmed.indices) {
        out += trimmed[i]
//        put - character at 3rd and 9th indicies
        if (i == 3 || i == 9 && i != 14) out += " "
    }
//    original - 345678901234564
//    transformed - 3456-7890123-4564
//    xxxx-xxxxxx-xxxxx
    /**
     * The offset translator should ignore the hyphen characters, so conversion from
     *  original offset to transformed text works like
     *  - The 4th char of the original text is 5th char in the transformed text. (i.e original[4th] == transformed[5th]])
     *  - The 11th char of the original text is 13th char in the transformed text. (i.e original[11th] == transformed[13th])
     *  Similarly, the reverse conversion works like
     *  - The 5th char of the transformed text is 4th char in the original text. (i.e  transformed[5th] == original[4th] )
     *  - The 13th char of the transformed text is 11th char in the original text. (i.e transformed[13th] == original[11th])
     */
    val creditCardOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 3) return offset
            if (offset <= 9) return offset + 1
            if (offset <= 15) return offset + 2
            return 17
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 4) return offset
            if (offset <= 11) return offset - 1
            if (offset <= 17) return offset - 2
            return 15
        }
    }
    return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
}

fun formatDinnersClub(text: AnnotatedString): TransformedText {
    val trimmed = if (text.text.length >= 14) text.text.substring(0..13) else text.text
    var out = ""

    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i == 3 || i == 9 && i != 13) out += " "
    }

//    xxxx-xxxxxx-xxxx
    val creditCardOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 3) return offset
            if (offset <= 9) return offset + 1
            if (offset <= 14) return offset + 2
            return 16
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 4) return offset
            if (offset <= 11) return offset - 1
            if (offset <= 16) return offset - 2
            return 14
        }
    }
    return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
}

fun formatOtherCardNumbers(text: AnnotatedString): TransformedText {

    val trimmed = if (text.text.length >= 16) text.text.substring(0..15) else text.text
    var out = ""

    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i % 4 == 3 && i != 15) out += " "
    }
    val creditCardOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 3) return offset
            if (offset <= 7) return offset + 1
            if (offset <= 11) return offset + 2
            if (offset <= 16) return offset + 3
            return 19
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 4) return offset
            if (offset <= 9) return offset - 1
            if (offset <= 14) return offset - 2
            if (offset <= 19) return offset - 3
            return 16
        }
    }
    return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
}

fun formatVerveCard(text: AnnotatedString): TransformedText {

    val trimmed = if (text.text.length >= 18) text.text.substring(0..17) else text.text
    var out = ""

    trimmed.forEachIndexed { index, c ->
        when (index) {
            4 -> out += " $c"
            8 -> out += " $c"
            12 -> out += " $c"
        }
    }

    val creditCardOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 4) return offset
            if (offset <= 8) return offset + 1
            if (offset <= 12) return offset + 2
            return offset + 3
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 4) return offset
            if (offset <= 9) return offset - 1
            if (offset <= 15) return offset - 2
            return offset - 3
        }
    }
    return TransformedText(AnnotatedString(out), creditCardOffsetTranslator)
}