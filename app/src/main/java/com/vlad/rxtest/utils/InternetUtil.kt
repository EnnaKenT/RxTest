package com.vlad.rxtest.utils

import android.content.Context
import android.net.ConnectivityManager

object InternetUtil {

    fun isInternetTurnOn(context: Context): Boolean {
        val connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectionManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}