package com.alexis.myanimecompanion.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.domain.Anime

class DetailsViewModelFactory(private val anime: Anime, private val animeRepository: AnimeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(anime, animeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
