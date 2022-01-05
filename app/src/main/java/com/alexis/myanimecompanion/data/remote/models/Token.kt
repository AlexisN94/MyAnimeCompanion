package com.alexis.myanimecompanion.data.remote.models

data class Token(
    val access_token: String = "",
    val expires_in: Int = 0,
    val refresh_token: String = "",
    val token_type: String = ""
)