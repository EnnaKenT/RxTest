package com.vlad.rxtest.retrofit

import com.vlad.rxtest.entity.response.SearchByDate
import com.vlad.rxtest.entity.response.UserResponse
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface AlgoliaApiService {
    @GET("api/v1/users/pg")
    fun getUser(): Observable<UserResponse>

    @GET("api/v1/search_by_date")
    fun searchByDate(@Query("page") page: Int,
                     @Query("tags") tags: String): Observable<SearchByDate>

    /**
     * Companion object to create the AlgoliaApiService
     */
    companion object Factory {
        fun create(): AlgoliaApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://hn.algolia.com/")
                    .build()

            return retrofit.create(AlgoliaApiService::class.java)
        }
    }
}