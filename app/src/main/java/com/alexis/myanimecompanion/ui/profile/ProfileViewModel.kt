package com.alexis.myanimecompanion.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.domain.DomainUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(val animeRepository: AnimeRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _user = MutableLiveData<DomainUser?>()
    val user: LiveData<DomainUser?>
        get() = _user

    private val _evtStartLogin = MutableLiveData<Boolean>()
    val evtStartLogin: LiveData<Boolean>
        get() = _evtStartLogin

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _loading.postValue(true)
                getUser()
                _loading.postValue(false)
            }
        }
    }

    private suspend fun getUser() {
        animeRepository.getUser()?.let {
            _user.postValue(it.getOrNull())
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

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(null)
            animeRepository.logout()
        }
    }
}
