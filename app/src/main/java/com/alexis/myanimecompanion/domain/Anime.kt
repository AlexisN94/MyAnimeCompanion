package com.alexis.myanimecompanion.domain

import android.os.Parcelable
import com.alexis.myanimecompanion.data.local.models.DatabaseAnime
import com.alexis.myanimecompanion.data.local.models.DatabaseCompleteAnime
import kotlinx.parcelize.Parcelize

@Parcelize
data class Anime(
    val id: Int,
    val title: String,
    val imageUrl: String,
    var myListStatus: AnimeStatus? = null,
    var details: AnimeDetails? = null
) : Parcelable

fun Anime.asDatabaseModel(): DatabaseCompleteAnime? {
    if (myListStatus == null || details == null) return null

    return DatabaseCompleteAnime(
        DatabaseAnime(id, title, imageUrl),
        myListStatus!!.asDatabaseModel(id),
        details!!.asDatabaseModel(id)
    )
}
