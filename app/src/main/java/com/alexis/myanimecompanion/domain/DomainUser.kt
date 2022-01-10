package com.alexis.myanimecompanion.domain

data class DomainUser(
    val id: Int = -1,
    val username: String? = null,
    val lastUpdate: String? = null,
    val isLoggedIn: Boolean = false
)
