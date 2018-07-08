package com.vlad.rxtest

import android.support.annotation.NonNull

interface MainActivityPresenter {

    fun initTask1()
    fun initTask2()
    fun initTask3()
    fun initTask4()
    fun initTask5()
    fun initTask6()
    fun unbindView()
    fun bindView(@NonNull mainActivity: MainActivity)
}