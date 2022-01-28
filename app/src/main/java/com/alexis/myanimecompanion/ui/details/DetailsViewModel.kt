package com.alexis.myanimecompanion.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error
import com.alexis.myanimecompanion.data.Error.*
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.ui.edit.EditEvent
import kotlinx.coroutines.launch

class DetailsViewModel(val animeWithoutDetails: Anime, private val animeRepository: AnimeRepository) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    private val _evtEdit = MutableLiveData(false)
    val evtEdit: LiveData<Boolean>
        get() = _evtEdit

    private val _evtShowStatus = MutableLiveData(false)
    val evtShowStatus: LiveData<Boolean>
        get() = _evtShowStatus

    private val _anime = MutableLiveData<Anime>()
    val anime: LiveData<Anime>
        get() = _anime

    init {
        viewModelScope.launch {
            _anime.value = animeWithoutDetails
            fetchAnimeDetails()
        }
    }

    private suspend fun fetchAnimeDetails() {
        animeRepository.getAnime(animeWithoutDetails).let { result ->
            if (result.isFailure) {
                handleError(result.errorOrNull()!!)
            } else {
                _anime.value = result.getOrNull()!!
            }
        }
    }

    private fun handleError(error: Error) {
        when (error) {
            Network -> setErrorMessage("A network error occurred")
            Generic -> setErrorMessage("An error occurred")
            NullUserStatus -> setErrorMessage("Failed to get your status")
            Authorization -> TODO()
            DatabaseQuery -> TODO()
        }
    }

    fun onEditClick() {
        _evtEdit.value = true
    }

    fun doneShowingEditDialog() {
        _evtEdit.value = false
    }

    private fun setErrorMessage(msg: String) {
        _errorMessage.value = msg
    }

    fun doneShowingErrorMessage() {
        _errorMessage.value = null
    }

    fun toggleStatus() {
        _evtShowStatus.value = _evtShowStatus.value?.let { !it } ?: true
    }

    fun addToList() {
        _errorMessage.value = "Not yet implemented"
    }

    fun onAnimeEdit(editEvent: EditEvent) {
        if (editEvent.isDelete) {
            _anime.value?.myListStatus = null
            _anime.postValue(_anime.value)
        } else {
            _anime.value = editEvent.anime!!
            _anime.postValue(_anime.value)
        }
    }
}
