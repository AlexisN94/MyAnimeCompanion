package com.alexis.myanimecompanion.ui.search

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error
import com.alexis.myanimecompanion.data.Result
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 24

class SearchViewModel(val animeRepository: AnimeRepository, private val resources: Resources) : ViewModel() {
    val liveSearchQuery = MutableLiveData<String?>()

    private val _resultList = MutableLiveData<MutableList<Anime>?>()
    val resultList: LiveData<MutableList<Anime>?>
        get() = _resultList

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _loadingMore = MutableLiveData(false)
    val loadingMore: LiveData<Boolean>
        get() = _loadingMore

    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?>
        get() = _statusMessage

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?>
        get() = _toastMessage

    private lateinit var currentSearchQuery: String
    private var currentPage = 0
    private var mLastVisibleItemPosition = 0

    fun onSearchClick() {
        updateStatusMessage(null)
        liveSearchQuery.value?.let {
            resetResultList()
            currentSearchQuery = it
            currentPage = 0
            mLastVisibleItemPosition = 0
            loadMore(0)
        }
    }

    fun loadMore(lastVisibleItemPosition: Int) {
        viewModelScope.launch {
            if (loading.value!! || loadingMore.value!!) cancel()

            val isFirstPage = currentPage == 0
            if (isFirstPage) setLoading() else updateLoadingMore(true)

            if (isFirstPage || !isFirstPage && lastVisibleItemPosition > mLastVisibleItemPosition) {
                search().let { result ->
                    if (result.isFailure) {
                        handleError(result.errorOrNull()!!, isFirstPage)
                    } else {
                        val list = result.getOrNull()!!
                        if (list.isEmpty() && isFirstPage) {
                            updateStatusMessage(resources.getString(R.string.no_results))
                        } else if (list.isNotEmpty()) {
                            appendToResultList(list, isFirstPage)
                            _resultList.postValue(_resultList.value)
                        }
                        currentPage++
                        mLastVisibleItemPosition = lastVisibleItemPosition
                    }
                }
            }
            if (isFirstPage) unsetLoading() else updateLoadingMore(false)
        }
    }

    private suspend fun search(): Result<List<Anime>> {
        animeRepository.search(currentSearchQuery, PAGE_SIZE, offset = currentPage.times(PAGE_SIZE)).let { result ->
            if (result.isFailure) {
                return Result.failure(result.errorOrNull()!!)
            } else {
                return Result.success(result.getOrNull()!!)
            }
        }
    }

    private fun appendToResultList(list: List<Anime>, isFirstPage: Boolean) {
        if (_resultList.value == null) _resultList.value = mutableListOf()
        _resultList.value?.addAll(list) ?: handleError(Error.Generic, isFirstPage)
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

    private fun handleError(error: Error, isFirstPage: Boolean) {
        if (isFirstPage) {
            when (error) {
                Error.Network -> updateStatusMessage(resources.getString(R.string.network_error_occurred))
                Error.EmptyList -> updateStatusMessage(resources.getString(R.string.empty_user_list))
                Error.BadRequest -> updateStatusMessage(resources.getString(R.string.invalid_search_query))
                else -> updateStatusMessage(resources.getString(R.string.generic_error_occurred))
            }

            resetResultList()
        } else {
            when (error) {
                Error.Network -> updateToastMessage(resources.getString(R.string.network_error_occurred))
                Error.EmptyList -> updateToastMessage(resources.getString(R.string.empty_user_list))
                Error.BadRequest -> updateToastMessage(resources.getString(R.string.invalid_search_query))
                else -> updateToastMessage(resources.getString(R.string.generic_error_occurred))
            }
        }
    }

    private fun updateStatusMessage(value: String?) {
        _statusMessage.value = value
    }

    private fun updateLoadingMore(value: Boolean) {
        _loadingMore.value = value
    }

    private fun updateToastMessage(text: String?) {
        _toastMessage.value = text
    }

    fun doneShowingToast() {
        updateToastMessage(null)
    }
}
