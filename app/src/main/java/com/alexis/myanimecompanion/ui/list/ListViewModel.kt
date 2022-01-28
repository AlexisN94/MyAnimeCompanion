package com.alexis.myanimecompanion.ui.list

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.ui.edit.EditEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ListViewModel(private val animeRepository: AnimeRepository, private val resources: Resources) : ViewModel() {

    private val _animeList = MutableLiveData<List<Anime>>()
    val animeList: LiveData<List<Anime>>
        get() = _animeList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?>
        get() = _statusMessage

    init {
        viewModelScope.launch(Dispatchers.IO) {
            unsetStatusMessage()
            setLoading()
            setAnimeList()
            unsetLoading()
            setAnimeDetails()
            // setStatusMessage()
        }
    }

    private suspend fun setAnimeList() {
        animeRepository.getAnimeList().let { result ->
            if (result.isFailure) {
                _statusMessage.postValue(result.errorOrNull()!!.name)
            } else {
                _animeList.postValue(result.getOrNull()!!)
            }
        }
    }

    private suspend fun setAnimeDetails() {
        val animeList = _animeList.value?.iterator()
        if (animeList != null) {
            for ((index, anime) in animeList.withIndex()) {
                animeRepository.getAnime(anime).let { result ->
                    if (result.isFailure) {
                        handleError(result.errorOrNull()!!)
                    } else {
                        _animeList.value?.elementAt(index)?.details = result.getOrNull()!!.details
                        _animeList.postValue(_animeList.value)
                    }
                }
            }
        }
    }

    private fun handleError(error: Error) {
        TODO()
    }

    private fun setLoading() {
        _loading.postValue(true)
    }

    private fun unsetLoading() {
        _loading.postValue(false)
    }

    private fun setStatusMessage() {
        val list = _animeList.value
        when {
            list == null -> {
                _statusMessage.postValue(resources.getString(R.string.network_error_occurred))
            }
            list.isEmpty() -> {
                _statusMessage.postValue(resources.getString(R.string.empty_user_list))
            }
        }
    }

    private fun unsetStatusMessage() {
        _statusMessage.postValue(null)
    }

    fun incrementWatchedEpisodes(anime: Anime) {
        viewModelScope.launch {
            var animeDetails = anime.details
            if (animeDetails == null
                || anime.myListStatus == null
                || animeDetails.numEpisodes == null
                || animeDetails.numEpisodes == 0
            )
                TODO("end coroutine")
            else if (anime.myListStatus.episodesWatched!! < animeDetails.numEpisodes) {
                anime.myListStatus.episodesWatched++
                animeRepository.insertOrUpdateAnimeStatus(anime)
                _animeList.value?.map {
                    if (it.id == anime.id)
                        anime
                }
            }
        }
    }

    fun decrementWatchedEpisodes(anime: Anime) {
        viewModelScope.launch {
            var animeDetails = anime.details
            if (animeDetails == null
                || anime.myListStatus == null
                || animeDetails.numEpisodes == null
                || animeDetails.numEpisodes == 0
            )
                cancel()
            else if (anime.myListStatus.episodesWatched > 0) {
                anime.myListStatus.episodesWatched++
                /*TODO updateAnimeStatus should return boolean for status check.
                   If all good, apply change to _animeList. Else, display failure to update error message.
                   Also, assert that remote values haven't changed before pushing update. */
                animeRepository.insertOrUpdateAnimeStatus(anime)
                _animeList.value?.map {
                    if (it.id == anime.id) {
                        anime
                    }
                }
            }
        }
    }

    fun onAnimeEdit(editEvent: EditEvent) {
        if (editEvent.isDelete) {
            _animeList.value = _animeList.value?.filter { anime ->
                anime.id != editEvent.animeId
            }
        } else {
            _animeList.value?.map { anime ->
                if (anime.id == editEvent.animeId) {
                    editEvent.anime
                } else {
                    anime
                }
            }
        }
    }
}
