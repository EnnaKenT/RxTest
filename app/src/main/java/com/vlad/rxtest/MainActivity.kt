package com.vlad.rxtest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vlad.rxtest.utils.InternetUtil

class MainActivity : AppCompatActivity(), MainActivityView {

    private lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainActivityPresenterImpl(this)
    }


    override fun onStart() {
        super.onStart()
        presenter.bindView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }

    override fun onResume() {
        super.onResume()

        if (InternetUtil.isInternetTurnOn(this)) {
//            val javaClass = JavaClass()
//            javaClass.initTask11()

            presenter.initTask63()
        }
    }
}