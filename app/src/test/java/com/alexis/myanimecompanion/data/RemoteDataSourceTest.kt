package com.alexis.myanimecompanion.data

import com.alexis.myanimecompanion.TokenStorageManager
import com.alexis.myanimecompanion.data.remote.MyAnimeListAPI
import com.alexis.myanimecompanion.data.remote.models.Details
import com.alexis.myanimecompanion.data.remote.models.SearchResult
import com.alexis.myanimecompanion.data.remote.models.User
import com.alexis.myanimecompanion.data.remote.models.UserAnimeList
import com.alexis.myanimecompanion.testutils.MockUtils.anyObject
import com.alexis.myanimecompanion.testutils.MockUtils.mockAnime
import com.alexis.myanimecompanion.testutils.MockUtils.mockDomainToken
import com.alexis.myanimecompanion.testutils.MockUtils.mockToken
import com.alexis.myanimecompanion.testutils.ReflectionUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class RemoteDataSourceTest {
    companion object {
        lateinit var myAnimeListApi: MyAnimeListAPI
        lateinit var tokenStorageManager: TokenStorageManager
        lateinit var remoteDataSource: RemoteDataSource

        @BeforeClass
        @JvmStatic
        fun setup() {
            tokenStorageManager = mock(TokenStorageManager::class.java)
            myAnimeListApi = mock(MyAnimeListAPI::class.java)

            `when`(tokenStorageManager.checkExpired()).thenReturn(false)
            `when`(tokenStorageManager.getToken()).thenReturn(mockDomainToken())

            remoteDataSource = spy(RemoteDataSource.getInstance(myAnimeListApi, tokenStorageManager))

            ReflectionUtils.setField(
                remoteDataSource,
                "codeVerifier",
                "someCodeVerifier"
            )
        }
    }

    @After
    fun resetMocks() {
        reset(tokenStorageManager, myAnimeListApi)

        `when`(tokenStorageManager.checkExpired()).thenReturn(false)
        `when`(tokenStorageManager.getToken()).thenReturn(mockDomainToken())
    }

    @Test
    fun testTrySearch() = runTest {
        `when`(myAnimeListApi.search(any(), any(), any(), any())).thenReturn(
            SearchResult()
        )

        val result = remoteDataSource.trySearch("", 0, 0, "")

        verify(myAnimeListApi).search("", 0, 0, "")

        assertEquals(SearchResult(), result.getOrNull())
    }

    @Test
    fun testTryGetAnimeDetails() = runTest {
        `when`(myAnimeListApi.getAnimeDetails(any(), anyInt(), anyString())).thenReturn(Details())

        val result = remoteDataSource.tryGetAnimeDetails(mockAnime())

        verify(myAnimeListApi).getAnimeDetails(any(), anyInt(), anyString())
        assert(result.isSuccess)
        assertEquals(Details(), result.getOrNull())
    }

    @Test
    fun testTryDeleteAnime_withoutToken() = runTest {
        `when`(tokenStorageManager.getToken()).thenReturn(null)

        val result = remoteDataSource.tryDeleteAnime(0)

        assert(result.isFailure)
        assert(result.errorOrNull()!! == Error.Authorization)
    }

    @Test
    fun testTryDeleteAnime_withToken() = runTest {

        val result = remoteDataSource.tryDeleteAnime(0)

        assert(result.isSuccess)
        verify(myAnimeListApi).deleteAnime("Bearer ", 0)
    }

    @Test
    fun testTryGetAnimeList() = runTest {
        `when`(myAnimeListApi.getUserAnimeList(anyString(), anyString(), anyInt())).thenReturn(
            UserAnimeList()
        )

        val result = remoteDataSource.tryGetAnimeList()

        verify(myAnimeListApi).getUserAnimeList(anyString(), anyString(), anyInt())
        assertEquals(UserAnimeList(), result.getOrNull())
    }

    @Test
    fun testTryGetUser() = runTest {
        `when`(myAnimeListApi.getUserProfile(anyString())).thenReturn(User())

        val result = remoteDataSource.tryGetUser()

        verify(myAnimeListApi).getUserProfile(anyString())
        assert(result.isSuccess)
        assertEquals(User(), result.getOrNull())
    }

    @Test
    fun testTryUpdateAnimeStatus_withToken() = runTest {
        val result = remoteDataSource.tryUpdateAnimeStatus(mockAnime())

        assert(result.isSuccess)
    }

    @Test
    fun testTryUpdateAnimeStatus_withoutToken() = runTest {
        `when`(remoteDataSource.getNonExpiredToken()).thenReturn(
            null
        )

        val result = remoteDataSource.tryUpdateAnimeStatus(mockAnime())

        assert(result.isFailure)
        assertEquals(Error.Authorization, result.errorOrNull())
    }

    @Test
    fun testTryUpdateAnimeStatus_WithNullAnimeStatus() = runTest {
        val result = remoteDataSource.tryUpdateAnimeStatus(mockAnime().apply { myListStatus = null })

        assert(result.isFailure)
        assertEquals(Error.NullUserStatus, result.errorOrNull())
    }

    @Test
    fun testHasValidToken_WithToken() = runTest {
        val result = remoteDataSource.hasValidToken()

        assertEquals(true, result)
    }

    @Test
    fun testHasValidToken_WithoutToken() = runTest {
        `when`(remoteDataSource.getNonExpiredToken()).thenReturn(null)

        val result = remoteDataSource.hasValidToken()

        assertEquals(false, result)
    }

    @Test
    fun testClearUser() {
        remoteDataSource.clearUser()

        verify(tokenStorageManager).clearToken()
    }

    @Test
    fun testGetNonExpiredToken_WithExpiredToken() = runTest {
        `when`(tokenStorageManager.checkExpired()).thenReturn(true)

        val result = remoteDataSource.getNonExpiredToken()

        verify(remoteDataSource).refreshAccessToken()
        assertEquals(mockDomainToken(), result)
    }

    @Test
    fun testRefreshAccessToken_WithoutToken() = runTest {
        `when`(tokenStorageManager.getToken()).thenReturn(null)

        remoteDataSource.refreshAccessToken()

        verifyNoInteractions(myAnimeListApi)
    }

    @Test
    fun testRefreshAccessToken_WithToken() = runTest {
        `when`(myAnimeListApi.refreshAccessToken(anyObject())).thenReturn(
            mockToken()
        )

        remoteDataSource.refreshAccessToken()

        verify(myAnimeListApi).refreshAccessToken(anyMap())
        verify(tokenStorageManager).setToken(anyObject())
    }

    @Test
    fun testGetAuthorizationURL() {
        // TODO
        //remoteDataSource.getAuthorizationURL()
    }

    @Test
    fun testRequestToken() = runTest {
        `when`(myAnimeListApi.getAccessToken(anyMap())).thenReturn(mockToken())

        val result = remoteDataSource.requestToken("someAuthCode")

        verify(tokenStorageManager).setToken(anyObject())
        assert(result.isSuccess)
    }
}
