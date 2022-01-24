package com.alexis.myanimecompanion.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error.*
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.coroutines.launch

class DetailsViewModel(val animeWithoutDetails: Anime, private val animeRepository: AnimeRepository) : ViewModel() {

    val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    private val _anime = MutableLiveData<Anime>()
    val anime: LiveData<Anime>
        get() = _anime

    init {
        _anime.value = animeWithoutDetails
        viewModelScope.launch {
            fetchAnimeDetails()
        }
    }

    private suspend fun fetchAnimeDetails() {
        animeRepository.getAnime(animeWithoutDetails).let { result ->
            if (result.isFailure) {
                when (result.errorOrNull()!!) {
                    Network -> setErrorMessage("A network error occurred")
                    Generic -> setErrorMessage("An error occurred")
                    NullUserStatus -> setErrorMessage("Failed to get your status")
                    Authorization -> TODO()
                    DatabaseQuery -> TODO()
                }
            } else {
                _anime.value = result.getOrNull()!!
            }
        }
    }

    private fun setErrorMessage(msg: String) {
        _errorMessage.value = msg
    }

    fun doneShowingErrorMessage() {
        _errorMessage.value = null
    }
}
