package com.alexis.myanimecompanion.domain

import android.os.Parcelable
import com.alexis.myanimecompanion.data.remote.models.AlternativeTitles
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Anime(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val synopsis: String? = null,
    val genres: String? = null,
    val releaseDate: Date? = null,
    val globalScore: Double? = null,
    val numEpisodes: Int? = null,
    var episodesWatched: Int = 0,
    var userScore: Int? = null,
    var userStatus: String? = null,
    val updatedAt: Date? = null,
    val status: String? = null,
    val alternativeTitles: String? = null,
) : Parcelable
