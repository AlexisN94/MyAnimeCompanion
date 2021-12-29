package com.alexis.myanimecompanion.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Anime(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val genre: String,
    val releaseDate: Date,
    val globalScore: Double,
    val releasedEpisodes: Int,
    val status: AnimeStatus? = null
) : Parcelable
