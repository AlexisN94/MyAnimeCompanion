package com.alexis.myanimecompanion.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import com.alexis.myanimecompanion.createEncryptedSharedPreferences
import com.alexis.myanimecompanion.data.local.models.asDomainUser
import com.alexis.myanimecompanion.domain.DomainUser

private const val USER_SP_FILENAME = "user_sp"
private const val CURRENT_USER_SP_KEY = "current_user_id"
private const val INVALID_USER_ID = Int.MIN_VALUE

class UserRepository private constructor() {
    private lateinit var sharedPreferences: EncryptedSharedPreferences
    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteDataSource: RemoteDataSource

    suspend fun fetchAndCacheUser(): DomainUser? {
        val remoteUser = remoteDataSource.getUser()

        val localUser =
            if (remoteUser == null) {
                // Not logged in or network error. Get local user if exists, otherwise return null
                var userId = getCurrentUserId() ?: return null
                localDataSource.getUser(userId)
            } else {
                // Logged in
                updateCurrentUserId(remoteUser.id)
                localDataSource.updateUser(remoteUser)
                localDataSource.getUser(remoteUser.id)
            }

        return localUser?.asDomainUser()
    }

    private fun updateCurrentUserId(userId: Int) {
        sharedPreferences.edit().putInt(CURRENT_USER_SP_KEY, userId)
    }

    private fun getCurrentUserId(): Int? {
        val userId = sharedPreferences.getInt(CURRENT_USER_SP_KEY, INVALID_USER_ID)
        if (userId == INVALID_USER_ID) return null
        return userId
    }

    fun logout() {
        val userId = getCurrentUserId() ?: return
        val user = localDataSource.getUser(userId) ?: return
        localDataSource.deleteUser(user)
        remoteDataSource.forgetUser()
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