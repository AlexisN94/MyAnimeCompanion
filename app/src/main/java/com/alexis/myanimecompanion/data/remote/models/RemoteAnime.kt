package com.alexis.myanimecompanion.data.remote.models

import com.squareup.moshi.Json

@Json(name = "node")
data class RemoteAnime(
    val id: Int = 0,
    @Json(name = "main_picture")
    val mainPicture: RemoteMainPicture = RemoteMainPicture(),
    val title: String = ""
)
