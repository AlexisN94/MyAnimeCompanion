package com.alexis.myanimecompanion.ui.list

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.domain.Anime
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
        viewModelScope.launch {
            /*_animeList.value = animeRepository.search("one")
            delay(4000)
            _animeList.value = animeRepository.search("kimi")*/
            unsetStatusMessage()
            setLoading()
            _animeList.value = mutableListOf(
                animeRepository.getAnime(28851),
                animeRepository.getAnime(42938),
                animeRepository.getAnime(11499),
                animeRepository.getAnime(11500) /* doesn't exist */,
                animeRepository.getAnime(33352)
            ).filterNotNull()
            unsetLoading()
            setStatusMessage(_animeList.value)
        }
    }

    private fun setLoading() {
        _loading.value = true
    }

    private fun unsetLoading() {
        _loading.value = false
    }

    private fun setStatusMessage(list: List<Anime>?) {
        when {
            list == null -> {
                _statusMessage.value = resources.getString(R.string.network_error_occurred)
            }
            list.isEmpty() -> {
                _statusMessage.value = resources.getString(R.string.empty_user_list)
            }
        }
    }

    private fun unsetStatusMessage() {
        _statusMessage.value = null
    }

    fun incrementWatchedEpisodes(anime: Anime) {
        viewModelScope.launch {
            if (anime.details == null
                || anime.myListStatus == null
                || anime.details.numEpisodes == null
                || anime.details.numEpisodes == 0
            )
                TODO("end coroutine")
            else if (anime.myListStatus.episodesWatched!! < anime.details.numEpisodes) {
                anime.myListStatus.episodesWatched++
                animeRepository.updateAnimeStatus(anime)
                _animeList.value?.map {
                    if (it.id == anime.id)
                        anime
                }
            }
        }
    }

    fun decrementWatchedEpisodes(anime: Anime) {
        viewModelScope.launch {
            if (anime.details == null
                || anime.myListStatus == null
                || anime.details.numEpisodes == null
                || anime.details.numEpisodes == 0
            )
                cancel()
            else if (anime.myListStatus.episodesWatched > 0) {
                anime.myListStatus.episodesWatched++
                /*TODO updateAnimeStatus should return boolean for status check.
                   If all good, apply change to _animeList. Else, display failure to update error message.
                   Also, assert that remote values haven't changed before pushing update. */
                animeRepository.updateAnimeStatus(anime)
                _animeList.value?.map {
                    if (it.id == anime.id) {
                        anime
                    }
                }
            }
        }
    }
}
