package com.example.iperf3client.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat.getSystemService

class NetworkUtils(context: Context) {

    private val connectivityManager = getSystemService(context, ConnectivityManager::class.java)
    val currentNetwork = connectivityManager?.activeNetwork

}