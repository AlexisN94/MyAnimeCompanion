package com.alexis.myanimecompanion.data.remote.models

data class UserAnimeList(
    val data: List<Data> = listOf(),
    val paging: Paging = Paging()
)