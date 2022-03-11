package com.alexis.myanimecompanion.data

import com.alexis.myanimecompanion.data.local.AnimeDatabase
import com.alexis.myanimecompanion.data.local.daos.AnimeDao
import com.alexis.myanimecompanion.data.local.daos.AnimeDetailsDao
import com.alexis.myanimecompanion.data.local.daos.AnimeStatusDao
import com.alexis.myanimecompanion.data.local.daos.UserDao
import com.alexis.myanimecompanion.data.local.models.DatabaseUser
import com.alexis.myanimecompanion.testutils.MockUtils.anyObject
import com.alexis.myanimecompanion.testutils.MockUtils.mockDatabaseAnimeList
import com.alexis.myanimecompanion.testutils.MockUtils.mockDatabaseCompleteAnime
import com.alexis.myanimecompanion.testutils.MockUtils.mockDatabaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class LocalDataSourceTest {
    companion object {
        lateinit var animeDatabase: AnimeDatabase
        lateinit var localDataSource: LocalDataSource

        @BeforeClass
        @JvmStatic
        fun setup() {
            animeDatabase = mock(AnimeDatabase::class.java)

            localDataSource = LocalDataSource.getInstance(animeDatabase)
        }
    }

    @Before
    fun mockDaos() {
        `when`(animeDatabase.userDao).thenReturn(
            mock(UserDao::class.java)
        )

        `when`(animeDatabase.animeDao).thenReturn(
            mock(AnimeDao::class.java)
        )

        `when`(animeDatabase.animeDetailsDao).thenReturn(
            mock(AnimeDetailsDao::class.java)
        )

        `when`(animeDatabase.animeStatusDao).thenReturn(
            mock(AnimeStatusDao::class.java)
        )
    }

    @After
    fun resetMocks() {
        reset(animeDatabase)
    }

    @Test
    fun testInsertOrUpdateUser_shouldInsert() = runTest {
        `when`(animeDatabase.userDao.update(anyObject())).thenReturn(0)

        withContext(Dispatchers.IO) {
            localDataSource.insertOrUpdateUser(DatabaseUser())
        }

        verify(animeDatabase.userDao).update(anyObject())
        verify(animeDatabase.userDao).insert(DatabaseUser())
    }

    @Test
    fun testInsertOrUpdateUser_shouldUpdate() = runTest {
        `when`(animeDatabase.userDao.update(anyObject())).thenReturn(1)

        withContext(Dispatchers.IO) {
            localDataSource.insertOrUpdateUser(DatabaseUser())
        }

        verify(animeDatabase.userDao).update(anyObject())
        verify(animeDatabase.userDao, times(0)).insert(DatabaseUser())
    }

    @Test
    fun testGetAnime() = runTest {
        `when`(animeDatabase.animeDao.getById(anyInt())).thenReturn(mockDatabaseCompleteAnime())

        val result = withContext(Dispatchers.IO) {
            localDataSource.getAnime(0)
        }

        animeDatabase.animeDao.getById(0)
        assertEquals(mockDatabaseCompleteAnime(), result)
    }

    @Test
    fun testGetUser() = runTest {
        `when`(animeDatabase.userDao.getUser()).thenReturn(mockDatabaseUser())

        val user = withContext(Dispatchers.IO) {
            localDataSource.getUser()
        }

        verify(animeDatabase.userDao).getUser()
        assertEquals(mockDatabaseUser(), user)
    }

    @Test
    fun testInsertAnimeList() = runTest {
        withContext(Dispatchers.IO) {
            localDataSource.insertOrUpdateAnimeList(mockDatabaseAnimeList())
        }

        verify(animeDatabase.animeDao).insertAll(anyObject())
        verify(animeDatabase.animeDetailsDao).insertAll(anyObject())
        verify(animeDatabase.animeStatusDao).insertAll(anyObject())
    }

    @Test
    fun testClearAllTables() = runTest {
        withContext(Dispatchers.IO) {
            localDataSource.clearAllTables()
        }

        verify(animeDatabase).clearAllTables()
    }

    @Test
    fun testDeleteAnime() = runTest {
        withContext(Dispatchers.IO) {
            localDataSource.deleteAnime(0)
        }

        verify(animeDatabase.animeDao).deleteById(0)
        verify(animeDatabase.animeStatusDao).deleteByAnimeId(0)
        verify(animeDatabase.animeDetailsDao).deleteByAnimeId(0)
    }
}
