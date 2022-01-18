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
            unsetStatusMessage()
            updateLoading(true)
            _animeList.value = mutableListOf(
                animeRepository.getAnime(28851),
                animeRepository.getAnime(42938),
                animeRepository.getAnime(11499),
                animeRepository.getAnime(33352)
            ).filterNotNull()
            updateLoading(false)
            updateStatusMessage(_animeList.value)
        }
    }

    private fun updateLoading(value: Boolean) {
        _loading.value = value
    }

    private fun updateStatusMessage(list: List<Anime>?) {
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
            if (anime.numEpisodes == null || anime.numEpisodes == 0) {
                TODO("end coroutine")
            } else if (anime.episodesWatched < anime.numEpisodes) {
                anime.episodesWatched++
                /*TODO updateAnimeStatus should return boolean for status check.
                  If all good, apply change to _animeList. Else, display failure to update error message.
                  Also, in updateAnimeStatus, assert that remote values haven't changed in the meantime. */
                animeRepository.updateAnimeStatus(anime)
                _animeList.value?.map {
                    if (it.id == anime.id) {
                        anime
                    }
                }
            }
        }
    }

    fun decrementWatchedEpisodes(anime: Anime) {
        viewModelScope.launch {
            if (anime.numEpisodes == null || anime.numEpisodes == 0) {
                cancel()
            } else if (anime.episodesWatched > 0) {
                anime.episodesWatched--
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
