package com.alexis.myanimecompanion.data

import android.content.Context
import com.alexis.myanimecompanion.data.local.AnimeDatabase
import com.alexis.myanimecompanion.data.local.models.DatabaseUser
import com.alexis.myanimecompanion.data.local.models.asDomainUser
import com.alexis.myanimecompanion.domain.DomainUser

class LocalDataSource private constructor() {
    private lateinit var animeDatabase: AnimeDatabase

    fun insertUser(user: DatabaseUser){
        animeDatabase.userDao.insert(user)
    }

    fun getUser(): DomainUser? {
        return animeDatabase.userDao.getUser()?.asDomainUser()
    }

    fun clearUser() {
        val user = animeDatabase.userDao.getUser()
        animeDatabase.userDao.delete(user)
    }

    fun updateUser(user: DatabaseUser) {
        animeDatabase.userDao.update(user)
    }

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