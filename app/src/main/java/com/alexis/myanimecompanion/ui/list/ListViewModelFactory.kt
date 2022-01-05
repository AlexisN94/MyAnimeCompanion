package com.alexis.myanimecompanion.ui.list

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexis.myanimecompanion.data.AnimeRepository

class ListViewModelFactory(private val animeRepository: AnimeRepository, private val resources: Resources) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            return ListViewModel(animeRepository, resources) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
