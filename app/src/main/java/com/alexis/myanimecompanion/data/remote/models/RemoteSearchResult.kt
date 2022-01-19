package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.domain.Anime

data class SearchResult(
    val listOfData: List<RemoteData>,
    val paging: RemotePaging
)

fun SearchResult.asListOfAnime(): List<Anime> {
    val animeList = mutableListOf<Anime>()

    for (data in this.listOfData) {
        animeList.add(data.node.asAnime())
    }
    return animeList
}
