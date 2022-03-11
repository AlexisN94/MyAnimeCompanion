package com.alexis.myanimecompanion.ui.list

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.*
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ListViewModel"

class ListViewModel(
    private val animeRepository: AnimeRepository,
    private val resources: Resources,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    val animeList = animeRepository.getAnimeList()

    val filterQuery = MutableLiveData("")

    val filteredAnimeList = MediatorLiveData<List<Anime>>().apply {
        addSource(animeList) {
            postValue(animeList.value?.filter {
                it.title.contains(filterQuery.value!!, true)
            })
        }
        addSource(filterQuery) {
            postValue(animeList.value?.filter {
                it.title.contains(filterQuery.value!!, true)
            })
        }
    }

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?>
        get() = _statusMessage

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            unsetStatusMessage()
            setLoading()
            refreshAnimeList()
            unsetLoading()
            // setStatusMessage()
        }
    }

    fun refreshAnimeList() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Test " + "refreshAnimeList() called")
            animeRepository.refreshAnimeList().let { result ->
                if (result.isFailure) {
                    _statusMessage.postValue(result.errorOrNull()!!.name)
                }
            }
        }
    }

    private fun handleError(error: Error) {
        // TODO
    }

    private fun setLoading() {
        _loading.postValue(true)
    }

    private fun unsetLoading() {
        _loading.postValue(false)
    }

    private fun setStatusMessage() {
        val list = filteredAnimeList.value
        when {
            list == null -> {
                _statusMessage.postValue(resources.getString(R.string.network_error_occurred))
            }
            list.isEmpty() -> {
                _statusMessage.postValue(resources.getString(R.string.empty_user_list))
            }
        }
    }

    private fun unsetStatusMessage() {
        _statusMessage.postValue(null)
    }

    fun editWatchedEpisodes(anime: Anime, editType: WatchedEpisodesEditType) {
        viewModelScope.launch(ioDispatcher) {
            if (canEditWatchedEpisodes(anime, editType)) {
                when (editType) {
                    WatchedEpisodesEditType.DECREMENT -> {
                        anime.myListStatus!!.episodesWatched--

                        if (anime.myListStatus!!.status == "completed") {
                            anime.myListStatus!!.status = "watching"
                        } else if (anime.myListStatus!!.episodesWatched == 0) {
                            anime.myListStatus!!.status = "plan_to_watch"
                        }
                    }

                    WatchedEpisodesEditType.INCREMENT -> {
                        anime.myListStatus!!.episodesWatched++

                        if (anime.myListStatus!!.episodesWatched == 1) {
                            anime.myListStatus!!.status = "watching"
                        } else if (anime.myListStatus!!.episodesWatched == anime.details!!.numEpisodes
                            && anime.details!!.status == "finished_airing"
                        ) {
                            anime.myListStatus!!.status = "completed"
                        }
                    }
                }

                animeRepository.updateAnimeStatus(anime).let { result ->
                    if (result.isFailure) {
                        handleError(result.errorOrNull()!!)
                    }
                }
            }
        }
    }

    private fun canEditWatchedEpisodes(anime: Anime, editType: WatchedEpisodesEditType): Boolean {
        val animeDetails = anime.details
        val myListStatus = anime.myListStatus

        return if (animeDetails != null && myListStatus != null && animeDetails.numEpisodes != 0) {
            when (editType) {
                WatchedEpisodesEditType.INCREMENT -> myListStatus.episodesWatched < animeDetails.numEpisodes
                WatchedEpisodesEditType.DECREMENT -> myListStatus.episodesWatched > 0
            }
        } else {
            false
        }
    }

    fun clearFilterQuery() {
        filterQuery.postValue("")
    }

    enum class WatchedEpisodesEditType {
        INCREMENT, DECREMENT
    }
}
