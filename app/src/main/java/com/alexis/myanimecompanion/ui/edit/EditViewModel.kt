package com.alexis.myanimecompanion.ui.edit

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error
import com.alexis.myanimecompanion.domain.Anime
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "EditViewModel"

class EditViewModel(var anime: Anime, private val animeRepository: AnimeRepository) : ViewModel() {

    // Must be public for two-way data binding
    val currentStatus = MutableLiveData<String?>()
    val userScore = MutableLiveData<Int?>()
    val episodesWatched = MutableLiveData<Int?>()

    private val _editEvent = MutableLiveData<EditEvent>()
    val editEvent: LiveData<EditEvent> get() = _editEvent

    private val _deleteClickEvent = MutableLiveData(false)
    val deleteClickEvent: LiveData<Boolean> get() = _deleteClickEvent

    init {
        viewModelScope.launch {
            animeRepository.getAnime(anime)?.let { result ->
                if (result.isFailure) {
                    handleError(result.errorOrNull()!!)
                } else {
                    updateUIValues(anime)
                }
            }
        }
    }

    private fun updateUIValues(anime: Anime) {
        episodesWatched.value = anime.myListStatus?.episodesWatched
        currentStatus.value = anime.myListStatus?.status
        userScore.value = anime.myListStatus?.score
    }

    private fun handleError(error: Error) {
        TODO("Not yet implemented")
    }

    fun updateAnime() {
        viewModelScope.launch(Dispatchers.IO) {
            animeRepository.insertOrUpdateAnimeStatus(anime).let { result ->
                if (result.isFailure) {
                    handleError(result.errorOrNull()!!)
                } else {
                    anime?.myListStatus?.let {
                        it.status = currentStatus.value ?: it.status
                        it.score = userScore.value ?: it.score
                        it.episodesWatched = episodesWatched.value ?: it.episodesWatched
                    }.also {
                        _editEvent.postValue(EditEvent.update(anime))
                    }
                }
            }
        }
    }

    fun onDeleteClick() {
        _deleteClickEvent.value = true
    }

    fun onCancelDelete() {
        _deleteClickEvent.value = false
    }

    fun onConfirmDelete() {
        _deleteClickEvent.value = false
        deleteAnime()
    }

    fun deleteAnime() {
        viewModelScope.launch(Dispatchers.IO) {
            animeRepository.deleteAnime(anime.id).let { result ->
                if (result.isFailure) {
                    handleError(result.errorOrNull()!!)
                } else {
                    _editEvent.postValue(EditEvent.delete(anime))
                }
            }
        }
    }

    fun onDialogDismiss() {
        _editEvent.value = null
    }
}

@Parcelize
class EditEvent private constructor(private val editType: EditType, val animeId: Int, val anime: Anime? = null) :
    Parcelable {
    val isUpdate: Boolean get() = editType == EditType.UPDATE
    val isDelete: Boolean get() = editType == EditType.DELETE

    companion object {
        fun delete(anime: Anime) = EditEvent(EditType.DELETE, anime.id)
        fun update(anime: Anime) = EditEvent(EditType.UPDATE, anime.id, anime)
    }

    enum class EditType {
        UPDATE, DELETE
    }
}
