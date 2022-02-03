package com.alexis.myanimecompanion.data.remote.models

import com.squareup.moshi.Json

data class Data(
    val node: Details = Details(),
    @Json(name = "list_status")
    val listStatus: MyListStatus = MyListStatus(),
)
