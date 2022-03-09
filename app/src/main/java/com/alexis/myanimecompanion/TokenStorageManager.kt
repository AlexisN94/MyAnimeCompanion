package com.alexis.myanimecompanion

import android.content.SharedPreferences
import com.alexis.myanimecompanion.domain.DomainToken
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private const val MIN_MILLIS_REMAINING_ALLOWED = 60 * 1000 // 1 min

class TokenStorageManager private constructor(private val sharedPreferences: SharedPreferences) {
    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val jsonAdapter: JsonAdapter<DomainToken> = moshi.adapter(DomainToken::class.java)

    fun setToken(domainToken: DomainToken) {
        val tokenJson = jsonAdapter.toJson(domainToken)
        sharedPreferences.edit()
            .putString("token", tokenJson)
            .apply()
    }

    fun getToken(): DomainToken? {
        if (!hasToken())
            return null

        val tokenJson = sharedPreferences.getString("token", null)
        return jsonAdapter.fromJson(tokenJson)
    }

    fun checkExpired(): Boolean {
        val token = getToken()
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
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private var INSTANCE: TokenStorageManager? = null

        fun getInstance(sharedPreferences: SharedPreferences): TokenStorageManager {
            synchronized(this) {
                return INSTANCE
                    ?: TokenStorageManager(sharedPreferences)
                        .also { instance ->
                            INSTANCE = instance
                        }
            }
        }
    }
}
