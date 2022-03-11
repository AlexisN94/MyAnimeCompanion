package com.alexis.myanimecompanion.data

import com.alexis.myanimecompanion.data.local.models.DatabaseAnime
import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeDetails
import com.alexis.myanimecompanion.data.local.models.DatabaseAnimeStatus
import com.alexis.myanimecompanion.data.local.models.DatabaseCompleteAnime
import com.alexis.myanimecompanion.data.remote.models.*
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.domain.AnimeDetails
import com.alexis.myanimecompanion.domain.AnimeStatus
import com.alexis.myanimecompanion.testutils.MockUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class AnimeRepositoryTest {
    companion object {
        lateinit var repository: AnimeRepository
        lateinit var localDataSource: LocalDataSource
        lateinit var remoteDataSource: RemoteDataSource

        @BeforeClass
        @JvmStatic
        fun setup() {
            localDataSource = mock(LocalDataSource::class.java)
            remoteDataSource = mock(RemoteDataSource::class.java)
            repository = AnimeRepository.getInstance(localDataSource, remoteDataSource)
        }
    }

//    lateinit var repository: AnimeRepository
//    @Mock lateinit var localDataSource: LocalDataSource
//    @Mock lateinit var remoteDataSource: RemoteDataSource
//
//    @Before
//    fun setup() {
//        repository = AnimeRepository.getInstance(localDataSource, remoteDataSource)
//    }

    @After
    fun resetMocks() {
        reset(remoteDataSource, localDataSource)
    }

    @Test
    fun `search() returns error on failure`() = runTest {
        val expectedError = Error.Authorization

        `when`(remoteDataSource.trySearch(anyString(), anyInt(), anyInt(), anyString()))
            .thenReturn(Result.failure(expectedError))

        val searchResult = repository.search("", 100, 0)

        assertEquals("search() returns success on failure!?", true, searchResult.isFailure)
        assertEquals(expectedError, searchResult.errorOrNull())
        reset(remoteDataSource)
    }

    @Test
    fun `search() returns listOfAnime on success`() = runTest {
        `when`(remoteDataSource.trySearch(anyString(), anyInt(), anyInt(), anyString()))
            .thenReturn(
                Result.success(
                    SearchResult(
                        listOf(
                            SearchResultData(
                                Node(0, "Anime1", MainPicture("urlL", "urlM"))
                            )
                        )
                    )
                )
            )

        val actualResult = repository.search("query", 100, 0)

        assertEquals("search() returns failure on success!?", true, actualResult.isSuccess)
        val expectedResult = listOf(Anime(0, "Anime1", "urlL"))
        assertEquals(expectedResult, actualResult.getOrNull())
    }

    @Test
    fun `logout() clears data`() {
        repository.logout()

        verify(localDataSource).clearAllTables()
        verify(remoteDataSource).clearUser()
    }

    @Test
    fun `isLoggedIn() returns true`() = runTest {
        `when`(remoteDataSource.hasValidToken()).thenReturn(true)

        val isLoggedIn = repository.isLoggedIn()

        assertEquals(true, isLoggedIn)
    }

    @Test
    fun `addAnime() fails when logged in and myListStatus is null`() = runTest {
        testAddAnime(isLoggedIn = true, null, successExpected = false)
    }

    @Test
    fun `addAnime() succeeds when logged in and myListStatus is not null`() = runTest {
        testAddAnime(isLoggedIn = true, AnimeStatus(), successExpected = true)
    }

    @Test
    fun `addAnime() fails when not logged in and myListStatus is null`() = runTest {
        testAddAnime(isLoggedIn = false, null, successExpected = false)
    }

    @Test
    fun `addAnime() succeeds when not logged in and myListStatus is not null`() = runTest {
        testAddAnime(isLoggedIn = false, AnimeStatus(), successExpected = true)
    }

    fun testAddAnime(isLoggedIn: Boolean, myListStatus: AnimeStatus?, successExpected: Boolean) = runTest {
        `when`(repository.isLoggedIn())
            .thenReturn(isLoggedIn)

        `when`(remoteDataSource.tryUpdateAnimeStatus(MockUtils.anyObject()))
            .thenReturn(
                if (myListStatus != null) {
                    Result.success()
                } else {
                    Result.failure(Error.NullUserStatus)
                }
            )

        val result = repository.addAnime(
            Anime(
                0,
                "Anime1",
                "",
                myListStatus,
                AnimeDetails(
                    "",
                    "",
                    Date(),
                    0.0,
                    0,
                    "",
                    ""
                )
            )
        )

        if (successExpected) {
            assertEquals(true, result.isSuccess)
        } else {
            assertEquals(true, result.isFailure)
        }
    }

    @Test
    fun `getAnime() queries remote and local sources`() {
        testGetAnime(
            remoteDataSourceFails = false,
            localDataSourceReturnsNull = false,
            successExpected = true
        )
    }

    @Test
    fun `getAnime() fails when remote source fails`() {
        testGetAnime(
            remoteDataSourceFails = true,
            localDataSourceReturnsNull = false,
            successExpected = false
        )
    }

    @Test
    fun `getAnime() succeeds when local source returns null`() {
        testGetAnime(
            remoteDataSourceFails = false,
            localDataSourceReturnsNull = true,
            successExpected = true
        )
    }

    fun testGetAnime(
        remoteDataSourceFails: Boolean,
        localDataSourceReturnsNull: Boolean,
        successExpected: Boolean
    ) = runTest {
        // Arrange
        `when`(repository.isLoggedIn())
            .thenReturn(false)

        `when`(remoteDataSource.tryGetAnimeDetails(MockUtils.anyObject())).thenReturn(
            if (remoteDataSourceFails) {
                Result.failure(Error.Generic)
            } else {
                Result.success(
                    Details(id = 1)
                )
            }
        )

        `when`(localDataSource.getAnime(anyInt())).thenReturn(
            if (localDataSourceReturnsNull) {
                null
            } else {
                DatabaseCompleteAnime(
                    DatabaseAnime(1, "", ""),
                    DatabaseAnimeStatus(1, 0, "", 0),
                    DatabaseAnimeDetails(1, "", "", "", 0.0, 0, "", "")
                )
            }
        )

        // Act
        val result = repository.getAnime(Anime(1, "", ""))


        // Assert
        verify(remoteDataSource).tryGetAnimeDetails(Anime(1, "", ""))

        if (!remoteDataSourceFails) {
            verify(localDataSource).getAnime(1)
        }

        if (successExpected) {
            assertEquals(true, result.isSuccess)
            assertEquals(1, result.getOrNull()!!.id)
        } else {
            assertEquals(true, result.isFailure)
        }
    }

    //    @Test
    //    fun `updateAnimeStatus() updates remote if logged in`() = runTest {
    //        `when`(repository.isLoggedIn()).thenReturn(true)
    //        `when`(remoteDataSource.getAnimeDetails(anyObject())).thenReturn(
    //            Result.success(
    //                Details(
    //                    id = 1, myListStatus = MyListStatus(updatedAt = "2000-01-02'T'00:00:00+00:00")
    //                )
    //            )
    //        )
    //
    //        repository.updateAnimeStatus(
    //            Anime(
    //                1, "", "",
    //                AnimeStatus(updatedAt = Calendar.getInstance().let {
    //                    it.set(2000, 0, 2)
    //                    it.time
    //                })
    //            )
    //        )
    //
    //        verify(remoteDataSource).updateAnimeStatus(anyObject())
    //        verify(localDataSource).insertOrUpdateAnime(anyObject())
    //    }
}
