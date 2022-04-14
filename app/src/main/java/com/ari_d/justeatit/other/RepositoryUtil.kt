package com.ari_d.justeatit.other

import co.paystack.android.exceptions.PaystackException
import com.bumptech.glide.load.engine.GlideException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException

inline fun <T> safeCall(action: () -> Resource<T>): Resource<T> {
    return try {
        action()
    } catch (e: FirebaseFirestoreException) {
        Resource.Error("Please check your internet connection.")
    } catch (e: FirebaseAuthException) {
        Resource.Error("Please check your internet connection.")
    } catch (e: FirebaseNetworkException) {
        Resource.Error("Please check your internet connection.")
    } catch (e: FirebaseException) {
        Resource.Error("An unknown error occurred.")
    } catch (e: PaystackException) {
        Resource.Error("An unexpected error occurred with this payment gateway.")
    } catch (e: GlideException) {
        Resource.Error("An unknown error occurred.")
    } catch (e: Exception) {
        Resource.Error(e.localizedMessage ?: "An unknown error occurred.")
    }
}