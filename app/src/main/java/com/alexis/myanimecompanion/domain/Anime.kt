package com.alexis.myanimecompanion.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Anime(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val myListStatus: AnimeStatus? = null,
    val details: AnimeDetails? = null
) : Parcelable
