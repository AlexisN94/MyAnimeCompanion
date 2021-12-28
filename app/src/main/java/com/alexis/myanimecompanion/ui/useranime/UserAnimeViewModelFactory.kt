package com.alexis.myanimecompanion.ui.useranime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserAnimeViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserAnimeViewModel::class.java)) {
            return UserAnimeViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
