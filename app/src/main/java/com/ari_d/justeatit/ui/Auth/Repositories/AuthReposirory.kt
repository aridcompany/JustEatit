package com.ari_d.justeatit.ui.Auth.Repositories

import com.ari_d.justeatit.other.Resource
import com.google.firebase.auth.AuthResult

interface AuthReposirory {

    suspend fun register(email : String, name: String, password: String) : Resource<AuthResult>

    suspend fun login(email : String, password: String) : Resource<AuthResult>

    suspend fun resetPassword(email : String) : Unit

}