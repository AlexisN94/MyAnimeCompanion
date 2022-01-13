package com.alexis.myanimecompanion.data.local.daos

import androidx.room.*
import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeStatus

@Dao
interface AnimeStatusDao {
    @Insert
    fun insert(animeStatus: DatabaseAnimeStatus)

    @Update
    fun update(animeStatus: DatabaseAnimeStatus): Int

    @Delete
    fun delete(animeStatus: DatabaseAnimeStatus)

    @Query("SELECT * FROM DatabaseAnimeStatus WHERE animeId = :animeId")
    fun getStatusByAnimeId(animeId: Int) : DatabaseAnimeStatus
}
