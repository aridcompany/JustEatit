package com.ari_d.justeatit.Extensions

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ari_d.justeatit.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView

val jcbRegex = Regex("^(?:2131|1800|35)[0-9]{0,}$")
val amexRegex = Regex("^3[47][0-9]{0,}\$")
val dinersRegex = Regex("^3(?:0[0-59]{1}|[689])[0-9]{0,}\$")
val visaRegex = Regex("^4[0-9]{0,}\$")
val verveRegex = Regex("^50610[59]0[0-9]{0,}\$")
val masterCardRegex = Regex("^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[01]|2720)[0-9]{0,}\$")
val maestroRegex = Regex("^(5[06789]|6)[0-9]{0,}\$")
val discoverRegex =
    Regex("^(6011|65|64[4-9]|62212[6-9]|6221[3-9]|622[2-8]|6229[01]|62292[0-5])[0-9]{0,}\$")

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

fun addCreditCardNumberTxtWatcher(
    context: Context,
    cardType: TextView,
    img: ImageView,
    et: TextInputEditText,
    et_layout: TextInputLayout,
    separator: Char
): TextWatcher {
    val animFade = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    val tw = object : TextWatcher {
        var mBlock = false
        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (mBlock)
                return
            var lastPos = et.selectionStart
            val oldStr = et.text.toString().replace(separator.toString(), "", false)
            var newFormattedStr = ""
            if (before > 0) {
                if (lastPos > 0 && et.text.toString()[lastPos - 1] == separator) lastPos--
            }
            mBlock = true
            oldStr.forEachIndexed { i, c ->
                if (oldStr.matches(amexRegex)) {
                    if (i == 4 || i == 10 || i == 15) {
                        newFormattedStr += separator
                    }
                    et.setMaxLength(17)
                    img.isVisible = true
                    img.setImageResource(R.drawable.american_express)
                    img.startAnimation(animFade)
                    cardType.text = "AMEX"
                } else if (oldStr.matches(verveRegex)) {
                    if (i == 6) {
                        newFormattedStr += separator
                    }
                    et.setMaxLength(19)
                    img.isVisible = true
                    img.setImageResource(R.drawable.verve)
                    img.startAnimation(animFade)
                    cardType.text = "VERVE"
                } else if (oldStr.matches(dinersRegex)) {
                    if (i == 4 || i == 10 || i == 14) {
                        newFormattedStr += separator
                    }
                    et.setMaxLength(16)
                    img.isVisible = true
                    img.setImageResource(R.drawable.dinners_card)
                    img.startAnimation(animFade)
                    cardType.text = "DINERS_CLUB"
                } else if (oldStr.matches(jcbRegex)) {
                    if (i > 0 && i % 4 == 0) {
                        newFormattedStr += separator
                    }
                    img.isVisible = true
                    img.setImageResource(R.drawable.jcb_card)
                    img.startAnimation(animFade)
                    cardType.text = "JCB"
                } else if (oldStr.matches(visaRegex)) {
                    if (i > 0 && i % 4 == 0) {
                        newFormattedStr += separator
                    }
                    et.setMaxLength(19)
                    img.isVisible = true
                    img.setImageResource(R.drawable.visa)
                    img.startAnimation(animFade)
                    cardType.text = "VISA"
                } else if (oldStr.matches(masterCardRegex)) {
                    if (i > 0 && i % 4 == 0) {
                        newFormattedStr += separator
                    }
                    et.setMaxLength(19)
                    img.isVisible = true
                    img.setImageResource(R.drawable.mastercard)
                    img.startAnimation(animFade)
                    cardType.text = "MASTERCARD"
                } else if (oldStr.matches(discoverRegex)) {
                    if (i > 0 && i % 4 == 0) {
                        newFormattedStr += separator
                    }
                    et.setMaxLength(19)
                    img.isVisible = true
                    img.setImageResource(R.drawable.discover)
                    img.startAnimation(animFade)
                    cardType.text = "DISCOVER"
                } else if (oldStr.matches(maestroRegex)) {
                    if (i > 0 && i % 4 == 0) {
                        newFormattedStr += separator
                    }
                    et.setMaxLength(19)
                    img.isVisible = true
                    img.setImageResource(R.drawable.maestro)
                    img.startAnimation(animFade)
                    cardType.text = "MAESTRO"
                } else if (oldStr.isEmpty()) {
                    img.isVisible = false
                } else {
                    if (i > 0 && i % 4 == 0) {
                        newFormattedStr += separator
                    }
                    img.isVisible = false
                    cardType.text = "UNKNOWN"
                }
                newFormattedStr += c
            }
            et.setText(newFormattedStr)
            if (before == 0) {
                if (et.text.toString()[lastPos - 1] == separator) lastPos++
            }
            et.setSelection(lastPos)
            mBlock = false
        }
    }
    et.addTextChangedListener(tw)
    return tw

}

fun TextInputEditText.setMaxLength(maxLength: Int) {
    filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
}

fun addCreditCardDateTxtWatcher(
    et: TextInputEditText,
    separator: Char
): TextWatcher {
    val tw = object : TextWatcher {
        var mBlock = false
        override fun afterTextChanged(s: Editable) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (mBlock)
                return
            var lastPos = et.selectionStart
            val oldStr = et.text.toString().replace(separator.toString(), "", false)
            var newFormattedStr = ""
            if (before > 0) {
                if (lastPos > 0 && et.text.toString()[lastPos - 1] == separator) lastPos--
            }
            mBlock = true
            oldStr.forEachIndexed { i, c ->
                if (i == 2) {
                    newFormattedStr += separator
                }
                et.setMaxLength(5)
                newFormattedStr += c
            }
            et.setText(newFormattedStr)
            if (before == 0) {
                if (et.text.toString()[lastPos - 1] == separator) lastPos++
            }
            et.setSelection(lastPos)
            mBlock = false
        }
    }
    et.addTextChangedListener(tw)
    return tw
}
