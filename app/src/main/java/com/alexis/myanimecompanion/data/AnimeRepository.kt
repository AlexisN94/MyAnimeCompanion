package com.alexis.myanimecompanion.data

class AnimeRepository private constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {

    companion object {
        private var INSTANCE: AnimeRepository? = null

        fun getInstance(localDataSource: LocalDataSource, remoteDataSource: RemoteDataSource): AnimeRepository {
            synchronized(this) {
                return INSTANCE
                    ?: AnimeRepository(localDataSource, remoteDataSource)
                        .also { animeRepo ->
                            INSTANCE = animeRepo
                        }
            }
        }
    }
}
