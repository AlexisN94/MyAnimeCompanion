package com.alexis.myanimecompanion.ui.search

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexis.myanimecompanion.data.AnimeRepository

class SearchViewModelFactory(private val animeRepository: AnimeRepository, private val resources: Resources) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(animeRepository, resources) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
