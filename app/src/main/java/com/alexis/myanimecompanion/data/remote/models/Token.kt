package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.domain.DomainToken

data class Token(
    var access_token: String,
    var expires_in: Int,
    var refresh_token: String,
    val token_type: String
)

fun Token.asDomainModel(): DomainToken {
    val expiresAt = System.currentTimeMillis() + expires_in
    return DomainToken(
        access_token,
        expiresAt,
        refresh_token,
        token_type
    )
}