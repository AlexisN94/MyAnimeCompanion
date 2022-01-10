package com.alexis.myanimecompanion.data

import android.content.Context
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.domain.DomainUser


class AnimeRepository private constructor() {
    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteDataSource: RemoteDataSource

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
        val remoteUserAsDatabaseUser = remoteDataSource.getUser()
        val localUser = localDataSource.getUser()

        if(localUser == null && remoteUserAsDatabaseUser != null) {
            localDataSource.insertUser(remoteUserAsDatabaseUser)
        } else if (remoteUserAsDatabaseUser != null) {
            localDataSource.updateUser(remoteUserAsDatabaseUser)
        }

        return localDataSource.getUser()
    }

    fun logout() {
        localDataSource.clearUser()
        remoteDataSource.clearUser()
    }

    fun getAuthorizationUrl(): String {
        return remoteDataSource.getAuthorizationURL()
    }

    suspend fun onAuthorizationCodeReceived(authorizationCode: String) {
        remoteDataSource.onAuthorizationCodeReceived(authorizationCode)
    }

    companion object {
        private var INSTANCE: AnimeRepository? = null

        fun getInstance(context: Context): AnimeRepository {
            synchronized(this) {
                return INSTANCE
                    ?: AnimeRepository()
                        .also { animeRepo ->
                            animeRepo.localDataSource = LocalDataSource.getInstance(context)
                            animeRepo.remoteDataSource = RemoteDataSource.getInstance(context)
                            INSTANCE = animeRepo
                        }
            }
        }
    }
}
