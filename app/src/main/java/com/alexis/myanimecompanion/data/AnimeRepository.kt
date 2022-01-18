package com.alexis.myanimecompanion.data

import android.content.Context
import com.alexis.myanimecompanion.domain.Anime


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
        /* TODO return complete? Anime object with fresh user-specific status */
        return remoteDataSource.getAnimeDetails(anime)
    }

    suspend fun getAnime(animeId: Int): Anime? {
        /* TODO return complete? Anime object with fresh user-specific status */
        return remoteDataSource.getAnimeDetails(animeId)
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
