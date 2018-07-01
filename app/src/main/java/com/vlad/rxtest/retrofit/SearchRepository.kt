package com.vlad.rxtest.retrofit

import com.vlad.rxtest.entity.response.SearchByDate
import com.vlad.rxtest.entity.response.UserResponse
import rx.Single

class SearchRepository(private val apiService: AlgoliaApiService) {

    fun searchByDate(page: Int): Single<SearchByDate> {
        return apiService.searchByDate(page, "story")
    }

    fun getUser(): Single<UserResponse> {
        return apiService.getUser()
    }
}