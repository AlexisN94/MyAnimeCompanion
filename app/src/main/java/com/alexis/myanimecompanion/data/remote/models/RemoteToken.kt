package com.alexis.myanimecompanion.data.remote.models

data class RemoteToken(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val token_type: String
)