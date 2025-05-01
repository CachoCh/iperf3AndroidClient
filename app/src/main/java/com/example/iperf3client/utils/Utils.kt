package com.example.iperf3client.utils

import android.content.Context
import android.content.pm.PackageManager

class Utils {
    companion object {
        fun getVersionName(context: Context): String? {
            val manager = context.packageManager
            val info = manager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
            return info.versionName
        }
    }
}