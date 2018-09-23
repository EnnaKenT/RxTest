package com.vlad.rxtest.utils

import android.view.View
import io.reactivex.Single

fun Single<Any>.getCurrentThread() {
    Single.just(Thread.currentThread())
}

fun View.setVisible() {
    visibility = View.VISIBLE
}