package com.alexis.myanimecompanion.data

import android.content.Context
import com.alexis.myanimecompanion.domain.Anime

class AnimeRepository private constructor() {
    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteDataSource: RemoteDataSource

    suspend fun search(query: String): List<Anime>? {
        return remoteDataSource.search(query)
    }

    suspend fun updateAnimeStatus(anime: Anime) {
    }

    suspend fun getAnime(anime: Anime): Anime? {
        return remoteDataSource.getAnimeDetails(anime)
    }

    suspend fun getAnime(animeId: Int): Anime? {
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
