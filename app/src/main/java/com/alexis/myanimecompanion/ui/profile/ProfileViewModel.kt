package com.alexis.myanimecompanion.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import kotlinx.coroutines.launch

class ProfileViewModel(val animeRepository: AnimeRepository) : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    private val _evtStartLogin = MutableLiveData<Boolean>()
    val evtStartLogin: LiveData<Boolean>
        get() = _evtStartLogin

    init {
        viewModelScope.launch {
            _loading.value = true
            getUser()
            _loading.value = false
        }
    }

    private fun getUser() {
        viewModelScope.launch {
            val user = animeRepository.getUser()
        }
    }

    fun onStartLogin() {
        _evtStartLogin.value = true
    }

    fun onStartLoginHandled() {
        _evtStartLogin.value = false
    }

    fun getAuthorizationUrl(): String {
        return animeRepository.getAuthorizationUrl()
    }
}