package com.alexis.myanimecompanion.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import com.alexis.myanimecompanion.createEncryptedSharedPreferences

private const val USER_SP_FILENAME = "user_sp"

class UserRepository private constructor() {
    private lateinit var sharedPreferences: EncryptedSharedPreferences
    private lateinit var localDataSource: LocalDataSource
    private lateinit var remoteDataSource: RemoteDataSource


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