package com.ari_d.justeatit.other

import com.bumptech.glide.load.engine.GlideException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: FirebaseFirestoreException) {
        Resource.Error("Unable to resolve host :(")
    } catch (e: FirebaseNetworkException) {
        Resource.Error("Please check your internet connection.")
    } catch (e: GlideException) {
        Resource.Error("An unknown error occurred.")
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "An unknown error occurred.")
    }
}