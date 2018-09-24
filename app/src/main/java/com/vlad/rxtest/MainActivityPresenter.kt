package com.vlad.rxtest

import android.support.annotation.NonNull

interface MainActivityPresenter {

    fun initTask1()
    fun initTask2()
    fun initTask3()
    fun initTask4()
    fun initTask5()
    fun initTask61()
    fun initTask62()
    fun initTask63()
    fun initTask7()
    fun initTask8()
    fun unbindView()
    fun bindView(@NonNull mainActivity: MainActivity)
}