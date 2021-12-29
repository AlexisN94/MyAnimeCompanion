package com.alexis.myanimecompanion.data

import android.content.Context
import com.alexis.myanimecompanion.data.local.AnimeDatabase
import com.alexis.myanimecompanion.data.local.daos.AnimeDao
import com.alexis.myanimecompanion.data.local.daos.AnimeStatusDao
import com.alexis.myanimecompanion.data.local.daos.UserDao
import com.alexis.myanimecompanion.domain.Anime

class LocalDataSource(private val context: Context) {
    private lateinit var animeDao: AnimeDao
    private lateinit var userDao: UserDao
    private lateinit var animeStatusDao: AnimeStatusDao

    init {
        val animeDatabase = AnimeDatabase.getInstance(context)
        animeDatabase.let {
            animeDao = it.animeDao
            userDao = it.userDao
            animeStatusDao = it.animeStatusDao
        }
    }
}
