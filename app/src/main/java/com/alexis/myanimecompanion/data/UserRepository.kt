package com.alexis.myanimecompanion.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import com.alexis.myanimecompanion.createEncryptedSharedPreferences
import com.alexis.myanimecompanion.data.local.models.asDomainUser
import com.alexis.myanimecompanion.domain.DomainUser

private const val USER_SP_FILENAME = "user_sp"

class UserRepository private constructor() {
    private lateinit var sharedPreferences: EncryptedSharedPreferences
    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteDataSource: RemoteDataSource

    suspend fun fetchAndCacheUser(): DomainUser? {
        val remoteUser = remoteDataSource.getUser()
        val localUser = localDataSource.getUser()

        if (localUser == null && remoteUser != null) {
            localDataSource.insertUser(remoteUser)
        } else if (remoteUser != null) {
            localDataSource.updateUser(
        }

        return localDataSource.getUser().asDomainUser()
    }

    fun logout() {
        val user =
            localDataSource.deleteUser()
        remoteDataSource.clearUser()
    }

    fun getAuthorizationUrl(): String {
        return remoteDataSource.getAuthorizationUrl()
    }

    suspend fun onAuthorizationCodeReceived(authorizationCode: String) {
        remoteDataSource.onAuthorizationCodeReceived(authorizationCode)
    }

    companion object {
        private var INSTANCE: UserRepository? = null

        fun getInstance(context: Context): UserRepository {
            synchronized(this) {
                return INSTANCE ?: UserRepository()
                    .also { instance ->
                        instance.localDataSource = LocalDataSource.getInstance(context)
                        instance.remoteDataSource = RemoteDataSource.getInstance(context)

                        instance.sharedPreferences = createEncryptedSharedPreferences(context, USER_SP_FILENAME)

                        INSTANCE = instance
                    }
            }
        }
    }
}