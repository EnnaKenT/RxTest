package com.vlad.rxtest.retrofit

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.vlad.rxtest.BuildConfig
import com.vlad.rxtest.entity.response.SearchByDate
import com.vlad.rxtest.entity.response.UserResponse
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AlgoliaApiService {

    @GET("api/v1/users/{user}")
    fun getUser(@Path("user") user: String): Single<UserResponse>

    @GET("api/v1/search_by_date?")
    fun searchByDate(@Query("page") page: Int, @Query("tags") tag: String): Single<SearchByDate>

    /**
     * Companion object to create the AlgoliaApiService
     */
    companion object Factory {
        fun create(): AlgoliaApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BuildConfig.ALGOLIA_API_HOST)
                    .build()

            return retrofit.create(AlgoliaApiService::class.java)
        }
    }
}