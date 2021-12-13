package com.ari_d.justeatit.ui.Auth.Repositories

import com.ari_d.justeatit.data.entities.User
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.other.safeCall
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultAuthRepository : AuthReposirory{

    val auth = FirebaseAuth.getInstance()
    val users = Firebase.firestore.collection("users")

    override suspend fun register(
        email: String,
        name: String,
        password: String
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid!!
                val user = User(uid, name)
                users.document(uid).set(user).await()
                Resource.Success(result)
            }
        }
    }

    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                Resource.Success(result)
            }
        }
    }

    override suspend fun resetPassword(email: String) {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.sendPasswordResetEmail(email).await()
                Resource.Success(result)
            }
        }
    }
}