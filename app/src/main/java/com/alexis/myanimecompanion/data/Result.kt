package com.alexis.myanimecompanion.data

/**
 * Adaptation of [kotlin-result][https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/src/kotlin/util/Result.kt]
 */
class Result<out T> private constructor(private val value: Any?) {
    val isSuccess: Boolean get() = value !is Error
    val isFailure: Boolean get() = value is Error

    fun getOrNull(): T? =
        when {
            isSuccess -> value as T
            else -> null
        }

    fun errorOrNull(): Error? =
        when (value) {
            is Failure -> value.error
            else -> null
        }

    companion object {
        fun <T> success(value: T? = null) = Result<T>(value)

        fun <T> failure(error: Error) = Result<T>(error)
    }

    private data class Failure(val error: Error)
}

enum class Error {
    Network,
    OutdatedLocalData,
    Generic,
    NullUserStatus,
    Authorization,
    DatabaseQuery
}
