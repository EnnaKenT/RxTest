package com.vlad.rxtest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vlad.rxtest.utils.InternetUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        InternetUtil.isInternetTurnOn(this)?.let { initTask() }
    }

    private fun initTask() {

    }
}