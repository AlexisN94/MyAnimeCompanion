package com.alexis.myanimecompanion.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error.*
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.coroutines.launch

class DetailsViewModel(var anime: Anime, private val animeRepository: AnimeRepository) : ViewModel() {

    val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    init {
        viewModelScope.launch {
            refreshAnime()
        }
    }

    suspend fun refreshAnime() {
        animeRepository.getAnime(anime).let { result ->
            if (result.isFailure) {
                when (result.errorOrNull()!!) {
                    Network -> setErrorMessage("A network error occurred")
                    Generic -> setErrorMessage("An error occurred")
                    NullUserStatus -> setErrorMessage("Failed to get your status")
                    Authorization -> TODO()
                    DatabaseQuery -> TODO()
                }
            }

            anime = result.getOrNull()!!
        }
    }

    private fun setErrorMessage(msg: String) {
        _errorMessage.value = msg
    }

    fun doneShowingErrorMessage() {
        _errorMessage.value = null
    }
}
