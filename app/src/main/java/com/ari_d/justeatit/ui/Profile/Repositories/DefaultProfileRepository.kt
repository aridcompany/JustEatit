package com.ari_d.justeatit.ui.Profile.Repositories

import android.widget.TextView
import com.ari_d.justeatit.data.entities.User
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.other.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultProfileRepository : ProfileRepository{

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val users = Firebase.firestore.collection("users")

    override suspend fun setNameandEmail(welcome: String, name: TextView, exclam: String, email: TextView) {
       return withContext(Dispatchers.IO) {
           safeCall {
               val user_email = currentUser?.email.toString()
               val user_name = currentUser?.displayName.toString()
               val result = currentUser?.let {
                   if (user_name == "null") {
                       name.text = welcome + exclam
                   } else {
                       name.text = welcome + " " + user_name + exclam
                   }
                   email.text = user_email
               }
               Resource.Success(result)
           }
       }
    }

    override suspend fun UpdateUserNameandEmail(name: String) {
        return withContext(Dispatchers.IO) {
            safeCall {
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }

                val result = currentUser?.let {
                    currentUser.updateProfile(profileUpdates)
                    val uid = currentUser.uid
                    val user = User(uid, name)
                    users.document(uid).set(user).await()
                }
                Resource.Success(result)
            }
        }
    }

    override suspend fun LogOut() {
        withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.signOut()
                Resource.Success(result)
            }
        }
    }
}