package com.ari_d.justeatit.ui.Profile.ViewModels

import android.content.Context
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.Event
import com.ari_d.justeatit.other.Resource
import com.ari_d.justeatit.ui.Profile.Repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _setNameStatus = MutableLiveData<Event<Resource<Unit>>>()
    val setNameStatus: LiveData<Event<Resource<Unit>>> = _setNameStatus

    private val _updateUserDetailsStatus = MutableLiveData<Event<Resource<Unit>>>()
    val updateUserDetailsStautus: LiveData<Event<Resource<Unit>>> = _updateUserDetailsStatus

    private val _logOutStatus = MutableLiveData<Event<Resource<Unit>>>()
    val logOutStatus: LiveData<Event<Resource<Unit>>> = _logOutStatus

    fun setNameandEmail(welcome: String, name: TextView, exclam: String, email: TextView) {
        _setNameStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.setNameandEmail(welcome, name, exclam, email)
            _setNameStatus.postValue(Event(Resource.Success(result)))
        }
    }

    fun updateUserDetails(name: String) {
        val error = if (name.isEmpty()) {
            applicationContext.getString(R.string.title_empty_input)
        } else null

        error?.let {
            _updateUserDetailsStatus.postValue(Event(Resource.Error(it)))
            return
        }
        _updateUserDetailsStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.UpdateUserNameandEmail(name)
            _updateUserDetailsStatus.postValue(Event(Resource.Success(result)))
        }
    }

    fun logOut() {
        _logOutStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.LogOut()
            _logOutStatus.postValue(Event(Resource.Success(result)))
        }
    }
}