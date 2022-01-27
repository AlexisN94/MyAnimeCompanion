package com.alexis.myanimecompanion.data.remote.models

import com.alexis.myanimecompanion.domain.DomainToken

data class RemoteToken(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val token_type: String
)

fun RemoteToken.asDomainModel(): DomainToken {
    val expiresAt = System.currentTimeMillis() + expires_in
    return DomainToken(
        access_token,
        expiresAt,
        refresh_token,
        token_type
    )
}
