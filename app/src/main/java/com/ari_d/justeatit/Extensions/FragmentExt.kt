package com.ari_d.justeatit.Extensions

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.snackbar(text: String) {
    Snackbar.make(
        requireView(),
        text,
        Snackbar.LENGTH_LONG
    ).show()
}

fun Fragment.alertDialog(title: String, message: String, icon: Drawable) {
    AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setIcon(icon)
        .create()
        .show()
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