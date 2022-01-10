package com.alexis.myanimecompanion.data

import android.content.Context
import com.alexis.myanimecompanion.TokenStorageManager
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.domain.DomainUser


class AnimeRepository private constructor() {
    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var tokenStorageManager: TokenStorageManager

    suspend fun search(q: String): List<Anime>? {
        return remoteDataSource.search(q)
    }

    suspend fun updateAnimeStatus(anime: Anime) {
        /* TODO Save changes to localDataSource and remoteDataSource if applicable */
    }

    suspend fun getAnime(anime: Anime): Anime? {
        /* TODO return complete? Anime object with fresh domainUser-specific status */
        return remoteDataSource.getAnimeDetails(anime)
    }

    suspend fun getAnime(animeId: Int): Anime? {
        /* TODO return complete? Anime object with fresh domainUser-specific status */
        return remoteDataSource.getAnimeDetails(animeId)
    }

    suspend fun getUser(): DomainUser? {
        var token = tokenStorageManager.getToken()
        if (tokenStorageManager.checkExpired()) {
            token = remoteDataSource.refreshAccessToken(token?.refreshToken)
            token?.let {
                tokenStorageManager.setToken(token)
            }
        }

        val remoteUserAsDatabaseUser = remoteDataSource.getUser(token?.accessToken)
        remoteUserAsDatabaseUser?.let {
            localDataSource.updateUser(remoteUserAsDatabaseUser)
        }

        return localDataSource.getUser()
    }

    fun logout() {
        localDataSource.clearUser()
        tokenStorageManager.clearToken()
    }

    fun getAuthorizationUrl(): String {
        return remoteDataSource.getAuthorizationURL()
    }

    suspend fun onAuthorizationCodeReceived(authorizationCode: String) {
        val token = remoteDataSource.getAccessToken(authorizationCode)
        if (token != null) {
            tokenStorageManager.setToken(token)
        }
    }

    companion object {
        private var INSTANCE: AnimeRepository? = null

        fun getInstance(context: Context): AnimeRepository {
            synchronized(this) {
                return INSTANCE
                    ?: AnimeRepository()
                        .also { animeRepo ->
                            animeRepo.localDataSource = LocalDataSource.getInstance(context)
                            animeRepo.remoteDataSource = RemoteDataSource.getInstance()
                            animeRepo.tokenStorageManager = TokenStorageManager.getInstance(context)
                            INSTANCE = animeRepo
                        }
            }
        }
    }
}
