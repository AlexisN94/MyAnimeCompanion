package com.alexis.myanimecompanion.repository.localData

import androidx.room.Dao
import androidx.room.Database
import androidx.room.RoomDatabase

@Dao
interface AnimeDAO

@Database(entities = [], version = 1)
abstract class AnimeDatabase : RoomDatabase()

