package com.alexis.myanimecompanion.data.remote.models

import com.squareup.moshi.Json

data class Data(
    val node: Node = Node(),
    @Json(name = "list_status")
    val listStatus: ListStatus = ListStatus(),
)