package com.alexis.myanimecompanion.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ListViewModel(private val animeRepository: AnimeRepository) : ViewModel() {

    private val _animeList = MutableLiveData<List<Anime>>()
    val animeList: LiveData<List<Anime>>
        get() = _animeList

    init {
        viewModelScope.launch {
            /*_animeList.value = animeRepository.search("one")
            delay(4000)
            _animeList.value = animeRepository.search("kimi")*/

            _animeList.value = mutableListOf(
                animeRepository.getAnime(28851),
                animeRepository.getAnime(42938),
                animeRepository.getAnime(11499),
                animeRepository.getAnime(11500) /* doesn't exist */,
                animeRepository.getAnime(33352)
            ).filterNotNull()
        }
    }

    fun incrementWatchedEpisodes(anime: Anime) {
        viewModelScope.launch {
            if (anime.numEpisodes == null || anime.numEpisodes == 0)
                TODO("end coroutine")
            else if (anime.episodesWatched < anime.numEpisodes) {
                anime.episodesWatched++
                /*TODO updateAnimeStatus should return boolean for status check.
                  If all good, apply change to _animeList. Else, display failure to update error message.
                  Also, in updateAnimeStatus, assert that remote values haven't changed in the meantime. */
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
            if (anime.numEpisodes == null || anime.numEpisodes == 0)
                cancel()
            else if (anime.episodesWatched > 0) {
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
