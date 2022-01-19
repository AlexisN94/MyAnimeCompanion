package com.alexis.myanimecompanion.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.coroutines.launch

class DetailsViewModel(var anime: Anime, private val animeRepository: AnimeRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            refreshAnime()
        }
    }

    suspend fun refreshAnime() {
        animeRepository.getAnime(anime)?.let {
            anime = it
        }
    }
}
