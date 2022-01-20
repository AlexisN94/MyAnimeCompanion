package com.alexis.myanimecompanion.ui.search

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 24

class SearchViewModel(val animeRepository: AnimeRepository, private val resources: Resources) : ViewModel() {
    val liveSearchQuery = MutableLiveData<String?>()

    private val _resultList = MutableLiveData<MutableList<Anime>?>()
    val resultList: LiveData<MutableList<Anime>?>
        get() = _resultList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _loadingMore = MutableLiveData<Boolean>()
    val loadingMore: LiveData<Boolean>
        get() = _loadingMore

    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?>
        get() = _statusMessage

    lateinit var currentSearchQuery: String
    private var currentPage = 0
    private var mLastVisibleItemPosition = 0

    fun onSearchClick() {
        updateStatusMessage(null)
        liveSearchQuery.value?.let {
            resetResultList()
            currentSearchQuery = it
            currentPage = 0
            search()
        }
    }

    fun loadMore(lastVisibleItemPosition: Int) {
        if (lastVisibleItemPosition > mLastVisibleItemPosition) {
            currentPage++
            search()
        }
    }

    private fun search() {
        viewModelScope.launch {
            if (currentPage == 0) setLoading() else updateLoadingMore(true)
            animeRepository.search(currentSearchQuery, PAGE_SIZE, offset = currentPage.times(PAGE_SIZE)).let { result ->
                if (result.isFailure) {
                    if (currentPage > 0) currentPage--
                    handleError(result.errorOrNull()!!)
                } else {
                    result.getOrNull()!!.also { list ->
                        if (list.isEmpty() && currentPage == 0) {
                            updateStatusMessage(resources.getString(R.string.no_results))
                        } else if (list.isNotEmpty()) {
                            appendToResultList(list)
                            _resultList.postValue(_resultList.value)
                        }
                    }
                }
            }
            if (currentPage == 0) unsetLoading() else updateLoadingMore(false)
        }
    }

    private fun appendToResultList(list: List<Anime>) {
        if (_resultList.value == null) _resultList.value = mutableListOf()
        _resultList.value?.addAll(list) ?: handleError(Error.Generic)
    }

    private fun resetResultList() {
        _resultList.value = mutableListOf()
    }

    fun clearQuery() {
        liveSearchQuery.value = ""
    }

    private fun setLoading() {
        _loading.value = true
    }

    private fun unsetLoading() {
        _loading.value = false
    }

    private fun handleError(error: Error) {
        when (error) {
            Error.Network -> updateStatusMessage(resources.getString(R.string.network_error_occurred))
            Error.EmptyList -> updateStatusMessage(resources.getString(R.string.empty_user_list))
            Error.BadRequest -> updateStatusMessage(resources.getString(R.string.invalid_search_query))
            else -> updateStatusMessage(resources.getString(R.string.generic_error_occurred))
        }

        resetResultList()
    }

    private fun updateStatusMessage(value: String?) {
        _statusMessage.value = value
    }

    private fun updateLoadingMore(value: Boolean) {
        _loadingMore.value = value
    }
}
