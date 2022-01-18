package com.alexis.myanimecompanion

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import com.alexis.myanimecompanion.domain.DomainToken
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private const val MIN_MILLIS_REMAINING_ALLOWED = 60 * 1000 // 1 min
private const val TOKEN_SP_KEY = "token_json"
private const val TOKEN_SP_FILENAME = "token_sp"

class TokenStorageManager private constructor() {
    private lateinit var sharedPreferences: EncryptedSharedPreferences
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val jsonAdapter: JsonAdapter<DomainToken> = moshi.adapter(DomainToken::class.java)

    fun updateToken(domainToken: DomainToken) {
        val tokenJson = jsonAdapter.toJson(domainToken)
        sharedPreferences.edit()
            .putString(TOKEN_SP_KEY, tokenJson)
            .apply()
    }

    fun fetchToken(): DomainToken? {
        val tokenJson = sharedPreferences.getString(TOKEN_SP_KEY, null) ?: return null
        return jsonAdapter.fromJson(tokenJson)
    }

    fun checkExpired(): Boolean {
        val token = fetchToken()
        return checkExpired(token)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun checkExpired(domainToken: DomainToken?): Boolean {
        if (domainToken == null) return false

        val millisLeft = domainToken.expiresAt - System.currentTimeMillis()
        return millisLeft <= MIN_MILLIS_REMAINING_ALLOWED
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
                            tokenStorageManager.sharedPreferences =
                                createEncryptedSharedPreferences(context, TOKEN_SP_FILENAME)

                            INSTANCE = tokenStorageManager
                        }
            }
        }
    }
}
