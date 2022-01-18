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
                updateLoading(true)
                fetchUser()
                updateLoading(false)
            }
        }
    }

    private suspend fun fetchUser() {
        animeRepository.fetchAndCacheUser()?.let {
            _user.postValue(it)
        }
    }

    private fun updateLoading(value: Boolean) {
        _loading.postValue(value)
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