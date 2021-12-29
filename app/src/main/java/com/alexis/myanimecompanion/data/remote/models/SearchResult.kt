package com.alexis.myanimecompanion.data.remote.models

data class SearchResult(
    val data: List<RemoteAnimeContainer> = listOf(),
    val paging: RemotePaging = RemotePaging()
)
