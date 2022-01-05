package com.alexis.myanimecompanion.data

import android.content.Context
import com.alexis.myanimecompanion.data.local.AnimeDatabase

class LocalDataSource private constructor() {
    private lateinit var animeDatabase: AnimeDatabase

    companion object {
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(context: Context): LocalDataSource {
            synchronized(this) {
                return INSTANCE ?: LocalDataSource().also { instance ->
                    instance.animeDatabase = AnimeDatabase.getInstance(context)
                    INSTANCE = instance
                }
            }
        }
    }
}