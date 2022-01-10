package com.alexis.myanimecompanion.ui.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.coroutines.launch

private const val TAG = "EditViewModel"

class EditViewModel(var anime: Anime, private val animeRepository: AnimeRepository) : ViewModel() {

    // Must be public for two-way data binding
    val currentStatus = MutableLiveData<String?>()
    val userScore = MutableLiveData<Int?>()
    val episodesWatched = MutableLiveData<Int?>()

    init {
        viewModelScope.launch {
            animeRepository.getAnime(anime)?.let {
                anime = it
                episodesWatched.value = anime.episodesWatched
                currentStatus.value = anime.userStatus
                userScore.value = anime.userScore
            }
        }
    }

    fun onSave() {
        viewModelScope.launch {
            anime?.let {
                it.userStatus = currentStatus.value
                it.userScore = userScore.value
                it.episodesWatched = episodesWatched.value ?: 0
                // TODO handle failure to update
                animeRepository.updateAnimeStatus(anime)
            }
        }
    }
}
