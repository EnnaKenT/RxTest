package com.vlad.rxtest.javaTest;

import com.vlad.rxtest.BuildConfig;
import com.vlad.rxtest.entity.response.SearchByDate;
import com.vlad.rxtest.entity.response.UserResponse;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Single;

public interface AlgoliaApiServiceJava {

    @GET("api/v1/api/v1/users/{user}")
    Single<UserResponse> getUser(@Path("user") String user);

    @GET("api/v1/search_by_date?")
    Single<SearchByDate> getSearchByDate(@Query("page") int page, @Query("tags") String tags);


    class Factory {

        public static AlgoliaApiServiceJava create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BuildConfig.ALGOLIA_API_HOST)
                    .build();

            return retrofit.create(AlgoliaApiServiceJava.class);
        }
    }


}
