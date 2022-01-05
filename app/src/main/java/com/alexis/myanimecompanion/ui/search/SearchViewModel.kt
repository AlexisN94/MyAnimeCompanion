package com.alexis.myanimecompanion.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.coroutines.launch

class SearchViewModel(val animeRepository: AnimeRepository) : ViewModel() {
    val searchQuery = MutableLiveData<String>()

    private val _resultList = MutableLiveData<List<Anime>?>()
    val resultList: LiveData<List<Anime>?>
        get() = _resultList

    fun search() {
        viewModelScope.launch {
            searchQuery.value?.let {
                val searchResults = animeRepository.search(it)
                searchResults?.let {
                    _resultList.value = searchResults
                }
            }
        }
    }
}