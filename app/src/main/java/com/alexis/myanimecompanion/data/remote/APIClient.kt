package com.alexis.myanimecompanion.data.remote

import com.alexis.myanimecompanion.data.RemoteDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object APIClient {
    const val MAL_CLIENT_ID = "cefa7feeaea5653f9eca4cd9c860ecca"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request: Request = chain.request()
                .newBuilder()
                .addHeader("X-MAL-CLIENT-ID", MAL_CLIENT_ID)
                .build()
            chain.proceed(request)
        }
        .build()

    val myAnimeListApi: MyAnimeListAPI = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(MyAnimeListAPI.BASE_URL)
        .client(httpClient)
        .build()
        .create(MyAnimeListAPI::class.java)
}
