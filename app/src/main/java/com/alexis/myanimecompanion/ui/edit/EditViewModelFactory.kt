package com.alexis.myanimecompanion.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.domain.Anime

class EditViewModelFactory(
    private val anime: Anime,
    private val animeRepository: AnimeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditViewModel::class.java)) {
            return EditViewModel(anime, animeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
