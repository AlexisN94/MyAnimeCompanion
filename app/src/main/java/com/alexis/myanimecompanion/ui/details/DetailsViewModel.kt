package com.alexis.myanimecompanion.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error
import com.alexis.myanimecompanion.data.Error.*
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.domain.AnimeStatus
import com.alexis.myanimecompanion.ui.edit.EditEvent
import kotlinx.coroutines.Dispatchers
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

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean>
        get() = _loading

    init {
        viewModelScope.launch {
            _loading.postValue(true)
            _anime.value = animeWithoutDetails
            fetchAnimeDetails()
            _loading.postValue(false)
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
            Network -> setToastMessage("A network error occurred")
            Generic -> setToastMessage("An error occurred")
            NullUserStatus -> setToastMessage("Failed to get your status")
            Authorization -> setToastMessage("Authorization error")
            DatabaseQuery -> setToastMessage("A local error occurred. Try refreshing")
        }
    }

    fun onEditClick() {
        _evtEdit.postValue(true)
    }

    fun doneShowingEditDialog() {
        _evtEdit.postValue(false)
    }

    private fun setToastMessage(msg: String) {
        _errorMessage.postValue(msg)
    }

    fun doneShowingErrorMessage() {
        _errorMessage.postValue(null)
    }

    fun toggleStatus() {
        _evtShowStatus.value = _evtShowStatus.value?.let { !it } ?: true
    }

    fun addToList() {
        viewModelScope.launch(Dispatchers.IO) {
            _anime.value?.let { it ->
                it.myListStatus = AnimeStatus()
                animeRepository.addAnime(it).let { result ->
                    if (result.isFailure) {
                        handleError(result.errorOrNull()!!)
                    } else {
                        _anime.postValue(it)
                    }
                }
            }
        }
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
