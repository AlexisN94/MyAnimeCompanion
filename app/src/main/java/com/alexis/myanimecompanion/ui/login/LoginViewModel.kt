package com.alexis.myanimecompanion.ui.login

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexis.myanimecompanion.data.AnimeRepository

class LoginViewModel(animeRepository: AnimeRepository) : ViewModel() {

    private val _webViewUrl = MutableLiveData<String>()
    val webViewUrl: LiveData<String>
        get() = _webViewUrl

    init {
        _webViewUrl.value = animeRepository.getAuthorizationUrl()
    }
}