package com.alexis.myanimecompanion.data

import com.alexis.myanimecompanion.MockUtils.anyObject
import com.alexis.myanimecompanion.MockUtils.mockDatabaseAnimeList
import com.alexis.myanimecompanion.MockUtils.mockDatabaseCompleteAnime
import com.alexis.myanimecompanion.MockUtils.mockDatabaseUser
import com.alexis.myanimecompanion.ReflectionUtils
import com.alexis.myanimecompanion.data.local.AnimeDatabase
import com.alexis.myanimecompanion.data.local.daos.AnimeDao
import com.alexis.myanimecompanion.data.local.daos.AnimeDetailsDao
import com.alexis.myanimecompanion.data.local.daos.AnimeStatusDao
import com.alexis.myanimecompanion.data.local.daos.UserDao
import com.alexis.myanimecompanion.data.local.models.DatabaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class LocalDataSourceTest {
    lateinit var animeDatabase: AnimeDatabase
    lateinit var localDataSource: LocalDataSource

    @Before
    fun setup() {
        animeDatabase = mock(AnimeDatabase::class.java)

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

        localDataSource = ReflectionUtils.invokeConstructor(LocalDataSource::class)

        ReflectionUtils.setField(localDataSource, "animeDatabase", animeDatabase)
    }

    @After
    fun closeDb() {
        animeDatabase.close()
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

        verify(animeDatabase.animeDao).insert(anyObject())
        verify(animeDatabase.animeDetailsDao).insert(anyObject())
        verify(animeDatabase.animeStatusDao).insert(anyObject())
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
