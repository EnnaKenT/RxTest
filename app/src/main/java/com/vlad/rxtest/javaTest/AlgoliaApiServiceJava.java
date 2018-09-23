package com.vlad.rxtest.javaTest;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.vlad.rxtest.BuildConfig;
import com.vlad.rxtest.entity.response.SearchByDate;
import com.vlad.rxtest.entity.response.UserResponse;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AlgoliaApiServiceJava {

    @GET("api/v1/users/{user}")
    Single<UserResponse> getUser(@Path("user") String user);

    @GET("api/v1/search_by_date?")
    Single<SearchByDate> getSearchByDate(@Query("page") int page, @Query("tags") String tags);


    class Factory {

        public static AlgoliaApiServiceJava create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BuildConfig.ALGOLIA_API_HOST)
                    .build();

            return retrofit.create(AlgoliaApiServiceJava.class);
        }
    }


}
