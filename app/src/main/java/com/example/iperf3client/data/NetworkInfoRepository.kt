package com.example.iperf3client.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager

class NetworkInfoRepository(context: Context) {
    private val connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val context = context

    fun getNetworkType(): String {

        return getNetwork(connectivityManager, context)
    }

    companion object {
        @Volatile private var instance: NetworkInfoRepository? = null
        fun getInstance(application: Context) =
            instance ?: synchronized(this) {
                instance ?: NetworkInfoRepository(application).also { instance = it }
            }
    }


    @SuppressLint("MissingPermission")
    fun getNetwork(connectivityManager: ConnectivityManager, context: Context): String {
        val nw = connectivityManager.activeNetwork ?: return "-"
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return "-"
        when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return "WIFI"
            //actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return "ETHERNET"
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                when (tm.dataNetworkType) {
                    TelephonyManager.NETWORK_TYPE_GPRS -> return "GPRS"
                    TelephonyManager.NETWORK_TYPE_EDGE -> return "EDGE"
                    TelephonyManager.NETWORK_TYPE_CDMA -> return "CDMA"
                    TelephonyManager.NETWORK_TYPE_1xRTT -> return "1xRTT"
                    TelephonyManager.NETWORK_TYPE_IDEN -> return ""
                    TelephonyManager.NETWORK_TYPE_GSM -> return "GSM"
                    TelephonyManager.NETWORK_TYPE_UMTS -> return "UMTS"
                    TelephonyManager.NETWORK_TYPE_EVDO_0 -> return "EVDO_0"
                    TelephonyManager.NETWORK_TYPE_EVDO_A -> return "EVDO_A"
                    TelephonyManager.NETWORK_TYPE_HSDPA -> return "HSDPA"
                    TelephonyManager.NETWORK_TYPE_HSUPA -> return "HSUPA"
                    TelephonyManager.NETWORK_TYPE_HSPA -> return "HSPA"
                    TelephonyManager.NETWORK_TYPE_EVDO_B -> return "EVDO_B"
                    TelephonyManager.NETWORK_TYPE_EHRPD -> return "EHRPD"
                    TelephonyManager.NETWORK_TYPE_HSPAP -> return "HSPAP"
                    TelephonyManager.NETWORK_TYPE_TD_SCDMA -> return "SCDMA"
                    TelephonyManager.NETWORK_TYPE_LTE -> return "LTE"
                    TelephonyManager.NETWORK_TYPE_IWLAN, 19  -> return "IWLAN"
                    TelephonyManager.NETWORK_TYPE_NR  -> return "NR"
                    else -> return "?"
                }
            }
            else -> return "?"
        }
    }
}