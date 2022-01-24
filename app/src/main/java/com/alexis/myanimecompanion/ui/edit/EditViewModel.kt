package com.alexis.myanimecompanion.ui.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.coroutines.launch

private const val TAG = "EditViewModel"

class EditViewModel(var anime: Anime, private val animeRepository: AnimeRepository) : ViewModel() {

    // Must be public for two-way data binding
    val currentStatus = MutableLiveData<String?>()
    val userScore = MutableLiveData<Int?>()
    val episodesWatched = MutableLiveData<Int?>()

    init {
        viewModelScope.launch {
            animeRepository.getAnime(anime)?.let { result ->
                if(result.isFailure){
                    handleError(result.errorOrNull()!!)
                } else {
                    val anime = result.getOrNull()!!
                    episodesWatched.value = anime.myListStatus?.episodesWatched
                    currentStatus.value = anime.myListStatus?.status
                    userScore.value = anime.myListStatus?.score
                }
            }
        }
    }

    private fun handleError(error: Error) {
        TODO("Not yet implemented")
    }

    fun onSave() {
        viewModelScope.launch {
            anime?.myListStatus?.let {
                it.status = currentStatus.value ?: it.status
                it.score = userScore.value ?: it.score
                it.episodesWatched = episodesWatched.value ?: it.episodesWatched
                // TODO handle failure to update
                animeRepository.insertOrUpdateAnimeStatus(anime)
            }
        }
    }
}
