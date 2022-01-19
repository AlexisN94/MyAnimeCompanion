package com.alexis.myanimecompanion.ui.search

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.coroutines.launch

class SearchViewModel(val animeRepository: AnimeRepository, private val resources: Resources) : ViewModel() {
    val searchQuery = MutableLiveData<String>()

    private val _resultList = MutableLiveData<List<Anime>?>()
    val resultList: LiveData<List<Anime>?>
        get() = _resultList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?>
        get() = _statusMessage

    fun search() {
        viewModelScope.launch {
            searchQuery.value?.let {
                updateStatusMessage(null)
                updateLoading(true)
                val searchResults: List<Anime>? = animeRepository.search(it)
                searchResults?.let {
                    _resultList.value = searchResults
                }
                updateLoading(false)
                updateStatusMessage(searchResults)
            }
        }
    }

    fun clearQuery() {
        searchQuery.value = ""
    }

    private fun updateLoading(value: Boolean) {
        _loading.value = value
    }

    private fun updateStatusMessage(searchResults: List<Anime>?) {
        when {
            searchResults == null -> {
                _statusMessage.value = resources.getString(R.string.network_error_occurred)
            }
            searchResults.isEmpty() -> {
                _statusMessage.value = resources.getString(R.string.empty_search_results)
            }
        }
    }
}
