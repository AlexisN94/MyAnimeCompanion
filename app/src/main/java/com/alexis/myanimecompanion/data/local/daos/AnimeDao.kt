package com.alexis.myanimecompanion.data.local.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.alexis.myanimecompanion.data.local.models.DatabaseAnime
import com.alexis.myanimecompanion.data.local.models.DatabaseCompleteAnime

@Dao
interface AnimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(databaseAnime: DatabaseAnime)

    @Update
    fun update(databaseAnime: DatabaseAnime): Int

    @Delete
    fun delete(databaseAnime: DatabaseAnime)

    @Transaction
    @Query("SELECT * FROM DatabaseAnime WHERE id = :animeId")
    fun getById(animeId: Int): DatabaseCompleteAnime

    @Query("DELETE FROM DatabaseAnime WHERE id = :animeId")
    fun deleteById(animeId: Int)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg animes: DatabaseAnime)

    @Transaction
    @Query("SELECT * FROM DatabaseAnime")
    fun getAll(): LiveData<List<DatabaseCompleteAnime>>
}
