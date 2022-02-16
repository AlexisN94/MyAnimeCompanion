package com.alexis.myanimecompanion.domain

data class DomainToken(
    var accessToken: String,
    var expiresAt: Long,
    var refreshToken: String,
    var tokenType: String
)
