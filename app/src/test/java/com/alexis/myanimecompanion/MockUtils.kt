package com.alexis.myanimecompanion

import com.alexis.myanimecompanion.data.local.models.*
import org.mockito.Mockito

object MockUtils {

    fun mockDatabaseCompleteAnime(): DatabaseCompleteAnime {
        return DatabaseCompleteAnime(
            DatabaseAnime(0, "", ""),
            DatabaseAnimeStatus(0, 0, "", 0, "2000-01-01'T'00:00:00+00:00"),
            DatabaseAnimeDetails(0, "", "", "", 0.0, 0, "", "")
        )
    }

    fun mockDatabaseAnimeList(): List<DatabaseCompleteAnime> {
        return listOf(
            DatabaseCompleteAnime(
                DatabaseAnime(0, "", ""),
                DatabaseAnimeStatus(0, 0, "", 0, "2000-01-01'T'00:00:00+00:00"),
                DatabaseAnimeDetails(0, "", "", "", 0.0, 0, "", "")
            ),
            DatabaseCompleteAnime(
                DatabaseAnime(1, "", ""),
                DatabaseAnimeStatus(1, 0, "", 0, "2000-01-01'T'00:00:00+00:00"),
                DatabaseAnimeDetails(1, "", "", "", 0.0, 0, "", "")
            ),
            DatabaseCompleteAnime(
                DatabaseAnime(2, "", ""),
                DatabaseAnimeStatus(2, 0, "", 0, "2000-01-01'T'00:00:00+00:00"),
                DatabaseAnimeDetails(2, "", "", "", 0.0, 0, "", "")
            ),
            DatabaseCompleteAnime(
                DatabaseAnime(3, "", ""),
                DatabaseAnimeStatus(3, 0, "", 0, "2000-01-01'T'00:00:00+00:00"),
                DatabaseAnimeDetails(3, "", "", "", 0.0, 0, "", "")
            )
        )
    }

    fun mockDatabaseUser(): DatabaseUser {
        return DatabaseUser(id = 0)
    }

    fun <T> anyObject(): T {
        return Mockito.any()
    }
}
