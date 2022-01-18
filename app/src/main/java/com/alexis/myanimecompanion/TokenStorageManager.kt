package com.alexis.myanimecompanion

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.alexis.myanimecompanion.domain.DomainToken
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private const val MIN_MILLIS_REMAINING_ALLOWED = 60 * 1000 // 1 min

class TokenStorageManager private constructor() {
    private lateinit var sharedPreferences: EncryptedSharedPreferences
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val jsonAdapter: JsonAdapter<DomainToken> = moshi.adapter(DomainToken::class.java)

    fun updateToken(domainToken: DomainToken) {
        val tokenJson = jsonAdapter.toJson(domainToken)
        sharedPreferences.edit()
            .putString("token_json", tokenJson)
            .apply()
    }

    fun getToken(): DomainToken? {
        if (!hasToken())
            return null

        val tokenJson = sharedPreferences.getString("token", null)
        return jsonAdapter.fromJson(tokenJson)
    }

    fun checkExpired(): Boolean {
        val token = fetchToken()
        return checkExpired(token)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun checkExpired(domainToken: DomainToken?): Boolean {
        if (domainToken == null)
            return false

        val millisLeft = domainToken.expiresAt - System.currentTimeMillis()
        return millisLeft <= MIN_MILLIS_REMAINING_ALLOWED
    }

    fun hasToken(): Boolean {
        val accessToken = sharedPreferences.getString("token", null)
        return accessToken != null
    }

    fun clearToken() {
        sharedPreferences.edit().clear()
    }

    companion object {
        private var INSTANCE: TokenStorageManager? = null

        fun getInstance(context: Context): TokenStorageManager {
            synchronized(this) {
                return INSTANCE
                    ?: TokenStorageManager()
                        .also { tokenStorageManager ->
                            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

                            tokenStorageManager.sharedPreferences = EncryptedSharedPreferences
                                .create(
                                    "token_sp",
                                    masterKeyAlias,
                                    context.applicationContext,
                                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                                ) as EncryptedSharedPreferences

                            INSTANCE = tokenStorageManager
                        }
            }
        }
    }
}
