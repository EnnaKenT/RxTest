package com.vlad.rxtest

import android.util.Log
import io.reactivex.SingleObserver
import io.reactivex.SingleSource

class CustomSingleSource : SingleSource<Any> {

    override fun subscribe(observer: SingleObserver<in Any>) {
        Log.i("duck", "Current thread: ${Thread.currentThread()}")
    }
}