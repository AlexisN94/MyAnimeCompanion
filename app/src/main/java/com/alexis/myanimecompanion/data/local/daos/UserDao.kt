package com.alexis.myanimecompanion.data.local.daos

import androidx.room.*
import com.alexis.myanimecompanion.data.local.models.DatabaseUser

@Dao
interface UserDao {
    @Insert
    fun insert(databaseUser: DatabaseUser)

    @Update
    fun update(databaseUser: DatabaseUser): Int

    @Delete
    fun delete(databaseUser: DatabaseUser)

    @Query("SELECT * FROM DatabaseUser")
    fun getUser(): DatabaseUser
}
