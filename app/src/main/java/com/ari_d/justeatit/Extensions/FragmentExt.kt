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
