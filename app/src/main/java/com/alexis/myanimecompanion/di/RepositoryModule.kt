package com.alexis.myanimecompanion.di

import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.LocalDataSource
import com.alexis.myanimecompanion.data.RemoteDataSource
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
    @Provides
    fun provideAnimeRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): AnimeRepository {
        return AnimeRepository.getInstance(localDataSource, remoteDataSource)
    }
}
