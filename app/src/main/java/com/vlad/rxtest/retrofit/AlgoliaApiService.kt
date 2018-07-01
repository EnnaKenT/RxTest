package com.vlad.rxtest.retrofit

import com.vlad.rxtest.BuildConfig
import com.vlad.rxtest.entity.response.SearchByDate
import com.vlad.rxtest.entity.response.UserResponse
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Single

interface AlgoliaApiService {

    @GET("api/v1/users/pg")
    fun getUser(): Single<UserResponse>

    @GET("api/v1/search_by_date?")
    fun searchByDate(@Query("page") page: Int, @Query("tags") tag: String): Single<SearchByDate>

    /**
     * Companion object to create the AlgoliaApiService
     */
    companion object Factory {
        fun create(): AlgoliaApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BuildConfig.ALGOLIA_API_HOST)
                    .build()

            return retrofit.create(AlgoliaApiService::class.java)
        }
    }
}