package com.alexis.myanimecompanion.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexis.myanimecompanion.data.AnimeRepository

class ProfileViewModelFactory(private val animeRepository: AnimeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModelFactory(animeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

