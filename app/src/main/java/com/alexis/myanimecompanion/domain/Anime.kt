package com.alexis.myanimecompanion.domain

import android.os.Parcelable
import com.alexis.myanimecompanion.data.local.models.DatabaseAnime
import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeWithStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class Anime(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val myListStatus: AnimeStatus? = null,
    var details: AnimeDetails? = null
) : Parcelable

fun Anime.asDatabaseModel(): DatabaseAnimeWithStatus? {
    return myListStatus?.let {
        DatabaseAnimeWithStatus(
            DatabaseAnime(id, title, imageUrl),
            myListStatus.asDatabaseModel(id)
        )
    }
}
