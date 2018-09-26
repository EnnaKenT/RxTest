package com.vlad.rxtest.utils

import android.util.Log
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.logThread(): Single<T> = map {
    Log.d("duck", "threadName ${Thread.currentThread()}")
    it
}

fun <T> Observable<T>.logThread(): Observable<T> = map {
    Log.d("duck", "threadName ${Thread.currentThread()}")
    it
}

fun <T> Maybe<T>.logThread(): Maybe<T> = map {
    Log.d("duck", "threadName ${Thread.currentThread()}")
    it
}

fun <T> Single<T>.customSchedulers(): Single<T> =
        subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())


fun <T> Observable<T>.customSchedulers(): Observable<T> =
        subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

fun <T> Maybe<T>.customSchedulers(): Maybe<T> =
        subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())