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

    private val _animeList = MutableLiveData<MutableList<Anime>>()
    val animeList: LiveData<List<Anime>>
        get() = _animeList as LiveData<List<Anime>>

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
                _animeList.postValue(result.getOrNull()!! as MutableList<Anime>)
            }
        }
    }

    private suspend fun setAnimeDetails() {
        val animeList = _animeList.value
        if (animeList != null) {
            for ((index, anime) in animeList.withIndex()) {
                animeRepository.getAnime(anime).let { result ->
                    if (result.isFailure) {
                        handleError(result.errorOrNull()!!)
                    } else {
                        _animeList.value?.set(index, result.getOrNull()!!)
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

    fun editWatchedEpisodes(anime: Anime, editType: WatchedEpisodesEditType) {
        viewModelScope.launch(Dispatchers.IO) {
            val animeIndex = _animeList.value?.indexOf(anime)

            if (animeIndex == null) {
                cancel()
            }

            if (canEditWatchedEpisodes(anime, editType)) {
                when (editType) {
                    WatchedEpisodesEditType.DECREMENT -> anime.myListStatus!!.episodesWatched--
                    WatchedEpisodesEditType.INCREMENT -> anime.myListStatus!!.episodesWatched++
                }

                animeRepository.insertOrUpdateAnimeStatus(anime).let { result ->
                    if (result.isFailure) {
                        handleError(result.errorOrNull()!!)
                    } else {
                        _animeList.value?.set(animeIndex!!, anime)
                    }
                }
            }
        }
    }

    private fun canEditWatchedEpisodes(anime: Anime, editType: WatchedEpisodesEditType): Boolean {
        val animeDetails = anime.details
        val myListStatus = anime.myListStatus

        return if (animeDetails != null && myListStatus != null && animeDetails.numEpisodes != 0) {
            when (editType) {
                WatchedEpisodesEditType.INCREMENT -> myListStatus.episodesWatched < animeDetails.numEpisodes
                WatchedEpisodesEditType.DECREMENT -> myListStatus.episodesWatched > 0
            }
        } else {
            false
        }
    }

    fun onAnimeEdit(editEvent: EditEvent) {
        if (editEvent.isDelete) {
            _animeList.value = _animeList.value?.filter { anime ->
                anime.id != editEvent.animeId
            } as MutableList<Anime>?
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

    enum class WatchedEpisodesEditType {
        INCREMENT, DECREMENT
    }
}
