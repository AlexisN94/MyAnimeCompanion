package com.alexis.myanimecompanion.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.ui.list.ListViewModel

class SearchViewModelFactory(private val animeRepository: AnimeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(animeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}