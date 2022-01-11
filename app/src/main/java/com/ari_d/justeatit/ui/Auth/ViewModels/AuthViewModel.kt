package com.ari_d.justeatit.ui.Auth.ViewModels

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.Constants.MIN_PASSWORD_LENGTH
import com.ari_d.justeatit.other.Event
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.ui.Auth.Repositories.AuthReposirory
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthReposirory,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _registerStatus = MutableLiveData<Event<Resource<AuthResult>>>()
    val registerStatus: LiveData<Event<Resource<AuthResult>>> = _registerStatus

    private val _loginStatus = MutableLiveData<Event<Resource<AuthResult>>>()
    val loginStatus: LiveData<Event<Resource<AuthResult>>> = _loginStatus

    private val _resetPasswordStatus = MutableLiveData<Event<Resource<Unit>>>()
    val restPasswordStatus: LiveData<Event<Resource<Unit>>> = _resetPasswordStatus


    fun resetPassword(email: String) {
        val error = if (email.isEmpty()) {
            applicationContext.getString(R.string.title_empty_email)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            applicationContext.getString(R.string.title_not_a_valid_email)
        } else null

        error?.let {
            _resetPasswordStatus.postValue(Event(Resource.Error(it)))
            return
        }
        _resetPasswordStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.resetPassword(email)
            _resetPasswordStatus.postValue(Event(Resource.Success(result)))
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            val error = applicationContext.getString(R.string.title_empty_input)
            _loginStatus.postValue(Event(Resource.Error(error)))
        } else {
            _loginStatus.postValue(Event(Resource.Loading()))
            viewModelScope.launch(dispatcher) {
                val result = repository.login(email, password)
                _loginStatus.postValue(Event(result))
            }
        }
    }

    fun register(email: String, name: String, password: String) {
        val error = if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            applicationContext.getString(R.string.title_empty_input)
        } else if (password.length < MIN_PASSWORD_LENGTH) {
            applicationContext.getString(R.string.title_password_too_short, MIN_PASSWORD_LENGTH)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            applicationContext.getString(R.string.title_not_a_valid_email)
        } else null

        error?.let {
            _registerStatus.postValue(Event(Resource.Error(it)))
            return
        }
        _registerStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.register(email, name, password)
            _registerStatus.postValue(Event(result))
        }
    }
}
