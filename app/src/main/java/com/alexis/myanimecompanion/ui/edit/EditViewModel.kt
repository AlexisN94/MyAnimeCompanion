package com.alexis.myanimecompanion.ui.edit

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.domain.AnimeStatus
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*

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

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> get() = _toastMessage

    init {
        viewModelScope.launch {
            updateUIValues(anime)
        }
    }

    private fun updateUIValues(anime: Anime) {
        anime.myListStatus = anime.myListStatus ?: AnimeStatus()

        episodesWatched.value = anime.myListStatus!!.episodesWatched
        currentStatus.value = anime.myListStatus!!.status
        userScore.value = anime.myListStatus!!.score
    }

    private fun handleError(error: Error) {
        handleError(error.name)
    }

    private fun handleError(errorMessage: String) {
        _toastMessage.postValue("Error saving: $errorMessage")
    }

    fun applyChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            if (anime.myListStatus == null) {
                cancel()
            }

            anime.myListStatus!!.updatedAt = Date()
            anime.myListStatus!!.score = userScore.value ?: anime.myListStatus!!.score
            anime.myListStatus!!.status = currentStatus.value ?: anime.myListStatus!!.status

            if (anime.myListStatus!!.status == "completed") {
                if (anime.details!!.status != "finished_airing") {
                    handleError("Anime is still airing. Can't be set as 'completed'")
                    cancel()
                }
                episodesWatched.postValue(anime.details!!.numEpisodes)
                anime.myListStatus!!.episodesWatched = anime.details!!.numEpisodes
            } else {
                anime.myListStatus!!.episodesWatched = episodesWatched.value ?: anime.myListStatus!!.episodesWatched
            }

            animeRepository.insertOrUpdateAnimeStatus(anime).let { result ->
                if (result.isFailure) {
                    handleError(result.errorOrNull()!!)
                } else {
                    anime.myListStatus?.let {
                        it.status = currentStatus.value ?: it.status
                        it.score = userScore.value ?: it.score
                        it.episodesWatched = episodesWatched.value ?: it.episodesWatched
                    }

                    _editEvent.postValue(EditEvent.update(anime))
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

    fun doneShowingToast() {
        _toastMessage.value = null
    }
}

/**
 * EditEvent is used to communicate to the previous fragment which kind of operation was performed on which anime.
 */
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
