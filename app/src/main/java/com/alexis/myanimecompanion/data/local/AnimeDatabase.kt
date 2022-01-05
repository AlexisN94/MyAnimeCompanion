package com.alexis.myanimecompanion.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alexis.myanimecompanion.data.local.daos.AnimeDao
import com.alexis.myanimecompanion.data.local.daos.AnimeStatusDao
import com.alexis.myanimecompanion.data.local.daos.UserDao
import com.alexis.myanimecompanion.data.local.models.DatabaseAnime
import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeStatus
import com.alexis.myanimecompanion.data.local.models.DatabaseUser

@Database(
    entities = [DatabaseUser::class, DatabaseAnime::class, DatabaseAnimeStatus::class],
    version = 1,
    exportSchema = false
)
abstract class AnimeDatabase : RoomDatabase() {
    abstract val animeDao: AnimeDao
    abstract val userDao: UserDao
    abstract val animeStatusDao: AnimeStatusDao

    companion object {
        private var INSTANCE: AnimeDatabase? = null

        fun getInstance(context: Context): AnimeDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AnimeDatabase::class.java,
                    "my_anime_list_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
