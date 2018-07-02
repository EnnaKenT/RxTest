package com.vlad.rxtest

import android.support.annotation.NonNull

interface MainActivityPresenter {

    fun initTask1()
    fun initTask2()
    fun initTask3()
    fun unbindView()
    fun bindView(@NonNull mainActivity: MainActivity)
}