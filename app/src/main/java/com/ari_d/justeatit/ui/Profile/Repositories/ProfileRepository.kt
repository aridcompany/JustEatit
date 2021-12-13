package com.ari_d.justeatit.ui.Profile.Repositories

import android.widget.TextView

interface ProfileRepository {

    suspend fun setNameandEmail(welcome: String, name: TextView, exclam: String, email: TextView) : Unit

    suspend fun UpdateUserNameandEmail(name: String) : Unit

    suspend fun LogOut() : Unit
}