package com.alexis.myanimecompanion.di

import android.content.Context
import android.content.SharedPreferences
import com.alexis.myanimecompanion.TokenStorageManager
import com.alexis.myanimecompanion.data.LocalDataSource
import com.alexis.myanimecompanion.data.RemoteDataSource
import com.alexis.myanimecompanion.data.local.AnimeDatabase
import com.alexis.myanimecompanion.data.remote.APIClient
import com.alexis.myanimecompanion.data.remote.MyAnimeListAPI
import dagger.Module
import dagger.Provides

@Module
class DataSourceModule {
    @Provides
    fun provideLocalDataSource(database: AnimeDatabase): LocalDataSource {
        return LocalDataSource.getInstance(database)
    }

    @Provides
    fun provideRemoteDataSource(
        myAnimeListApi: MyAnimeListAPI,
        tokenStorageManager: TokenStorageManager
    ): RemoteDataSource {
        return RemoteDataSource.getInstance(myAnimeListApi, tokenStorageManager)
    }

    @Provides
    fun provideMyAnimeListAPI(): MyAnimeListAPI {
        return APIClient.myAnimeListApi
    }

    @Provides
    fun provideAnimeDatabase(context: Context): AnimeDatabase {
        return AnimeDatabase.getInstance(context)
    }

    @Provides
    fun provideTokenStorageManager(sharedPreferences: SharedPreferences): TokenStorageManager {
        return TokenStorageManager.getInstance(sharedPreferences)
    }
}
