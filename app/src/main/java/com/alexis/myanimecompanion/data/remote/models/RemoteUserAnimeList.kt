package com.alexis.myanimecompanion.data.remote.models

data class RemoteUserAnimeList(
    val data: List<RemoteData>,
    val paging: RemotePaging
)
